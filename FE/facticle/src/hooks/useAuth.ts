import { useContext } from "react";
import { AuthContext } from "../context";

// useAuth를 따로 분리하는 것은 다른 컴포넌트에서 useAuth를 사용할 때, AuthContext를 import하지 않아도 되게 하기 위함
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};