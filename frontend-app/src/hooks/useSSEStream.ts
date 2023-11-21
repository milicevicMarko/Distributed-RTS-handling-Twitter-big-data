import { useEffect, useState } from "react";
import {
  RealtimeInputData,
  RealtimeRawData,
  tweets_endpoint,
} from "../utils/contants";

export const useSSEStream = () => {
  const sessionId = localStorage.getItem("sessionId");
  const url = `${tweets_endpoint}?session=${sessionId}`;
  const [data, setData] = useState<RealtimeInputData>(null);
  const [stream, setStream] = useState<any>(null);

  const startStream = () => {
    if (!!sessionId && !stream) {
      const tempStream = new EventSource(url);
      setStream(tempStream);

      tempStream.onmessage = (event: any) => {
        console.warn(event);
        if (!!event?.data) {
          const rawData: RealtimeRawData = JSON.parse(event.data);
          if (!!rawData && rawData.sessionId === sessionId) {
            const parsed: Map<string, number> = new Map(
              Object.entries(rawData.message).map(([key, value]) => [
                String(key),
                Number(value),
              ])
            );
            setData(parsed);
          }
        }
      };
      tempStream.onerror = (error: any) => {
        console.error("Server sent events: ", error);
      };
    }
  };

  const stopStream = () => {
    if (!!sessionId && !!stream) {
      stream.close();
      setStream(null);
    }
  };

  useEffect(() => {
    return () => {
      stopStream();
    };
  }, []);

  return { data, startStream, stopStream };
};
