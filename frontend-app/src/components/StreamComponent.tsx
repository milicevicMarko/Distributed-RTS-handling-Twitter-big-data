import { ResponsiveContainer } from "recharts";
import { Button, Grid, Stack, Typography } from "@mui/material";
import { RealtimeDataProps, createRealtimeDataProps } from "../utils/contants";

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSSEStream } from "../hooks/useSSEStream";
import { PaperContainer } from "./PaperContainer";
import { RealtimeMultiChart } from "./RealtimeMultiChart";

export default function Chart() {
  const { data, startStream, stopStream } = useSSEStream();
  const terms: string[] = JSON.parse(localStorage.getItem("terms") ?? "[]");

  const [parsedData, setParsedData] = useState<RealtimeDataProps>({
    data: [],
  });

  useEffect(() => {
    if (!!data) {
      const fdata = new Map();
      terms.forEach((item) => {
        fdata.set(item, 0);
      });

      [...data.entries()]
        .filter((item) => {
          return terms.includes(item[0]);
        })
        .map((item) => {
          fdata.set(item[0], item[1]);
        });
      setParsedData(createRealtimeDataProps(fdata));
    }
  }, [data]);

  const navigate = useNavigate();

  const [isRunning, setIsRunning] = useState<boolean>(false);

  const handleStart = () => {
    setIsRunning(true);
    startStream();
  };
  const handleStop = () => {
    setIsRunning(false);
    stopStream();
  };
  const handleEdit = () => {
    handleStop();
    navigate("/edit");
  };

  return (
    <>
      <Typography variant="h3" component="div">
        Terms being tracked:
      </Typography>
      <ResponsiveContainer>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <PaperContainer elevation={3}>
              <Stack direction="row" spacing={2}>
                {!!terms &&
                  terms.map((item, index) => {
                    return (
                      <Typography variant="h6" component="div" key={index}>
                        {item}
                      </Typography>
                    );
                  })}
              </Stack>
            </PaperContainer>
          </Grid>
          <Grid item xs={12}>
            <Stack direction="row" spacing={2} justifyContent={"center"}>
              <Button
                variant="outlined"
                color="success"
                disabled={isRunning || terms.length === 0}
                onClick={handleStart}
              >
                Start
              </Button>
              <Button variant="outlined" color="primary" onClick={handleEdit}>
                Edit
              </Button>
              <Button
                variant="outlined"
                color="error"
                disabled={!isRunning}
                onClick={handleStop}
              >
                Stop
              </Button>
            </Stack>
          </Grid>
          <Grid item xs={12}>
            <RealtimeMultiChart data={parsedData.data} />
          </Grid>
        </Grid>
      </ResponsiveContainer>
    </>
  );
}
