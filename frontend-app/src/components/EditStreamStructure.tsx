import {
  Button,
  Grid,
  Paper,
  Stack,
  TextField,
  Tooltip,
  Typography,
  styled,
} from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PaperContainer } from "./PaperContainer";
import { start_endpoint, structure_endpoint } from "../utils/contants";

const StackItem = styled(Paper)(({ theme }) => ({
  backgroundColor: "#1A2027",
  ...theme.typography.body2,
  padding: theme.spacing(5),
  margin: theme.spacing(1),
  textAlign: "center",
  color: theme.palette.text.secondary,
}));

export function EditStructure() {
  // todo needs revisiting
  const [value, setValue] = useState("");
  const initialList = JSON.parse(localStorage.getItem("terms") ?? "[]");

  const [list, setList] = useState<string[]>(initialList);

  const isEmptyValue = value === "";
  const isListFull = list.length >= 5;
  const isListEmpty = list.length === 0;

  const ButtonLabelAdd = isListFull ? "Max 5 terms" : "Add";
  const ButtonLabelContinue = "Continue";

  const handleInput = (event: React.FocusEvent<HTMLInputElement>) => {
    if (event.target.value != value) {
      setValue(event.target.value);
    }
  };

  const handleOnKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      handleAddToList();
    }
  };

  const handleAddToList = () => {
    if (!!value) {
      setList((list) => [...list, value]);
      setValue("");
    }
  };

  const handleRemoveFromList = (index: number) => {
    setList((list) => list.filter((item, i) => i !== index));
  };

  const navigate = useNavigate();
  const routeChange = () => {
    let path = `/stream`;
    navigate(path);
  };

  const handleContinue = () => {
    const sessionId = localStorage.getItem("sessionId");
    localStorage.setItem("terms", JSON.stringify(list));
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ sessionId: sessionId, terms: list }),
    };
    fetch(start_endpoint, requestOptions)
      .then((response) => {
        routeChange();
      })
      .catch((error) => {
        console.error(error);
      });
  };

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <PaperContainer>
          <Typography variant="h4" component="h2">
            Enter the terms you would like to track
          </Typography>
        </PaperContainer>
      </Grid>
      <Grid item xs={6} style={{ height: "100%" }}>
        <PaperContainer>
          <Stack spacing={2}>
            <TextField
              id="standard-basic"
              label="Term"
              variant="standard"
              onChange={handleInput}
              onKeyDown={handleOnKeyDown}
              placeholder="Enter term to track"
              autoComplete="off"
              value={value}
            />
            <Stack direction="row" spacing={2}>
              <Button
                variant="outlined"
                color="success"
                onClick={handleAddToList}
                disabled={isEmptyValue || isListFull}
              >
                {ButtonLabelAdd}
              </Button>
              <Button
                variant="outlined"
                color="primary"
                onClick={handleContinue}
                disabled={isListEmpty}
              >
                {ButtonLabelContinue}
              </Button>
            </Stack>
          </Stack>
        </PaperContainer>
      </Grid>
      <Grid item xs={6} style={{ height: "100%" }}>
        <PaperContainer>
          <Stack spacing={2}>
            {list.map((item, index) => {
              return (
                <StackItem key={index} elevation={3}>
                  <Tooltip title={`Remove ${item} from the term list`}>
                    <Button
                      variant="text"
                      color="error"
                      onClick={() => handleRemoveFromList(index)}
                    >
                      {item}
                    </Button>
                  </Tooltip>
                </StackItem>
              );
            })}
          </Stack>
        </PaperContainer>
      </Grid>
    </Grid>
  );
}
