// import { ToggleButton } from "@mui/material";
// import { useState } from "react";
// import { RealtimeChartProps, RealtimeMultiChartProps } from "../utils/contants";

// export const useHideRealtimeData = ({ data }: RealtimeChartProps) => {
//   const { term } = data[0];

//   const [disabled, setDisabled] = useState<string[]>([]);
//   const [selected, setSelected] = useState<boolean>(true);

//   const IndividualPoints = () => {
//     const handleToggle = (event: any) => {
//       if (selected) {
//         setDisabled((disabled) => disabled.filter((item) => item !== term));
//       } else {
//         setDisabled((disabled) => [...disabled, term]);
//       }
//       setSelected(!selected);
//     };

//     return (
//       <ToggleButton
//         value={term}
//         selected={true}
//         onChange={handleToggle}
//         key={term}
//       >
//         {term}
//       </ToggleButton>
//     );
//   };

//   return { IndividualPoints, disabled };
// };
