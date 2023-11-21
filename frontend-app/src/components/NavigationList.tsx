import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import DashboardIcon from "@mui/icons-material/Dashboard";
import PeopleIcon from "@mui/icons-material/People";
import Twitter from "@mui/icons-material/Twitter";
import { List, Divider } from "@mui/material";
import { Fragment } from "react";
import { Link } from "react-router-dom";
import { CatchingPokemon, Info } from "@mui/icons-material";

export const NavigationList = () => {
  const links = [
    {
      name: "Home",
      to: "/",
      icon: <DashboardIcon />,
    },
    {
      name: "Stream",
      to: "/stream",
      icon: <Twitter />,
    },
    {
      name: "Contact",
      to: "/contact",
      icon: <PeopleIcon />,
    },
    {
      name: "About",
      to: "/about",
      icon: <Info />,
    },
  ];

  return (
    <List component="nav">
      <Fragment>
        {links.map((item, index) => {
          return (
            <ListItemButton key={index}>
              <Link to={item.to}>
                <ListItemIcon>{item.icon}</ListItemIcon>
              </Link>
              <ListItemText primary={item.name} />
            </ListItemButton>
          );
        })}
      </Fragment>
      <Divider sx={{ my: 1 }} />
    </List>
  );
};
