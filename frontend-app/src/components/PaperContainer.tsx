import { Paper, styled } from "@mui/material";

export const PaperContainer = styled(Paper)(({ theme }) => ({
  backgroundColor: "#1A2027",
  ...theme.typography.body2,
  padding: theme.spacing(5),
  margin: theme.spacing(1),
  textAlign: "center",
  justifyContent: "center",
  display: "flex",
  color: theme.palette.text.secondary,
}));
