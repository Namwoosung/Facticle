import { createContext, useEffect, useState } from "react";
import HttpService from "../services/htttp.service";
import { setupAxiosInterceptors } from "../services/interceptors";
import { useNavigate } from "react-router-dom";

interface AuthContextType {
    isAuthenticated: boolean | false;
    nickname: string | null;
    profileImage: string | null;
    login: (token: string) => void;
    logout: () => void;
    getUserProfile: (nickname: string, profileImage: string) => void;
    updateProfile: (nickname: string, profileImage: string | null) => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const navigate = useNavigate();
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [nickname, setNickname] = useState<string | null>(null);
    const [profileImage, setProfileImage] = useState<string | null>(null);

    const login = (token: string) => {
        HttpService.addJWTToken(token);
        setIsAuthenticated(true);
    };

    const logout = () => {
        HttpService.removeJWTToken();
        setIsAuthenticated(false);
    };

    const updateProfile = async (nickname: string, profileImage: string | null) => {
        setNickname(nickname);
        setProfileImage(profileImage);
    };

    const getUserProfile = (nickname: string, profileImage: string) => {
        setNickname(nickname);
        setProfileImage(profileImage);
    }

    useEffect(() => {
        setupAxiosInterceptors(() => {
            logout();
            //navigate('/login');
        });
    }, []);

    return (
        <AuthContext.Provider value={{ isAuthenticated, nickname, profileImage, login, logout, getUserProfile, updateProfile }}>
            {children}
        </AuthContext.Provider>
    );
};


