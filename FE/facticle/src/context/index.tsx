import { createContext, useContext, useState } from "react";
import HttpService from "../services/htttp.service";

interface AuthContextType {
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const login = (token: string) => {
        HttpService.addJWTToken(token);
    };

    const logout = () => {
        HttpService.removeJWTToken();
    };

    return (
        <AuthContext.Provider value={{ login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};
