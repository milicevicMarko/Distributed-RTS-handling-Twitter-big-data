import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";
import { Typography } from "@mui/material";
import { RealtimeDataItem } from "../utils/contants";

export const RealTimeChart: React.FC<RealtimeDataItem> = ({ term, amount }) => {
  return (
    <>
      <Typography variant="h6" component="div">
        {term}
      </Typography>
      <LineChart width={800} height={600} data={[amount]}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="term" />
        <YAxis dataKey="amount" />
        <Tooltip />
        <Line type="monotone" dataKey="amount" stroke="#8884d8" />
      </LineChart>
    </>
  );
};
