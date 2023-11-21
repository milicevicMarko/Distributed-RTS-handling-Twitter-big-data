const api = "http://localhost:8083";

export const session_endpoint = api + "/session";
export const structure_endpoint = api + "/structure";
export const start_endpoint = api + "/start";
export const stop_endpoint = api + "/stop";
export const tweets_endpoint = api + "/tweets";

export type RealtimeRawData = { sessionId: string; message: string };
export type RealtimeInputData = Map<string, number> | null;

export const createRealtimeDataProps = (
  data: RealtimeInputData
): RealtimeDataProps => {
  const result: RealtimeDataProps = {
    data: [],
  };
  data?.forEach((value, key) => {
    result.data.push({ term: key, amount: value });
  });
  return result;
};

export interface RealtimeDataItem {
  term: string;
  amount: number;
}

export interface RealtimeDataProps {
  data: RealtimeDataItem[];
}
