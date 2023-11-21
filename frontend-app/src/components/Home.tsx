import { session_endpoint } from "../utils/contants";

import {
  Button,
  CssBaseline,
  Box,
  Typography,
  Grid,
  Paper,
} from "@mui/material";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();
  const routeChange = () => {
    let path = `/edit`;
    navigate(path);
  };

  const checkSessionExists = () => !!localStorage.getItem("sessionId");

  const createSession = async () => {
    const response = await fetch(session_endpoint);
    const sessionId = await response.text();
    localStorage.setItem("sessionId", sessionId);
  };

  const getSession = () => {
    if (!checkSessionExists()) {
      createSession();
    }

    const sessionId = localStorage.getItem("sessionId");
    localStorage.setItem("terms", JSON.stringify([]));
    return sessionId;
  };

  const handleButtonClick = () => {
    getSession();
    routeChange();
  };

  return (
    <Grid container component="main" sx={{ height: "80vh" }}>
      <CssBaseline />
      <Grid
        item
        xs={false}
        sm={4}
        md={7}
        sx={{
          backgroundImage:
            "url(https://cdn.cms-twdigitalassets.com/content/dam/help-twitter/twitter_logo_blue.png.twimg.768.png)",
          backgroundRepeat: "no-repeat",
          backgroundColor: (t) =>
            t.palette.mode === "light"
              ? t.palette.grey[50]
              : t.palette.grey[900],
          backgroundSize: "cover",
          backgroundPosition: "center",
        }}
      />
      <Grid
        item
        xs={12}
        sm={8}
        md={5}
        component={Paper}
        elevation={10}
        square
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center", // Center content vertically
        }}
      >
        <Box sx={{ my: 8, mx: 4 }}>
          <Typography component="h1" variant="h5">
            No need to sign in!
          </Typography>
          <Box
            component="form"
            noValidate
            onClick={handleButtonClick}
            sx={{ mt: 1 }}
          >
            <Button fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
              Start session
            </Button>
          </Box>
        </Box>
      </Grid>
    </Grid>
  );
};

export default Home;
