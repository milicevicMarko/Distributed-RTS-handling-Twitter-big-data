import { ThemeProvider } from "@emotion/react";
import { dark } from "./utils/theme";
import Dashboard from "./components/Dashboard";
import { BrowserRouter } from "react-router-dom";

function App() {
  return (
    <ThemeProvider theme={dark}>
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
