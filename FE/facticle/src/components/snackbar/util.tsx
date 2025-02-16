import Snackbar from "./index";
import { createRoot } from "react-dom/client";

export function showSnackbar(message: string, icon?: React.ReactNode) {
  const snackbarRoot = document.createElement("div");
  document.body.appendChild(snackbarRoot);

  const root = createRoot(snackbarRoot);
  root.render(<Snackbar message={message} icon={icon}/>);

  setTimeout(() => {
    root.unmount();
    document.body.removeChild(snackbarRoot);
  }, 3500); // 애니메이션 고려
}
