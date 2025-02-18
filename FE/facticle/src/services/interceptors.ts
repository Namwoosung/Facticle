import HttpService from "./htttp.service";
import { AxiosRequestConfig, AxiosResponse, AxiosError } from "axios";
import { useAuth } from "../context/index";

export const setupAxiosInterceptors = (onUnauthenticated: () => void): void => {
  const onRequestSuccess = async (config: AxiosRequestConfig): Promise<AxiosRequestConfig> => {
    return config;
  };

  const onRequestFail = (error: AxiosError): Promise<never> => Promise.reject(error);

  HttpService.addRequestInterceptor(onRequestSuccess, onRequestFail);

  const onResponseSuccess = (response: AxiosResponse): AxiosResponse => response;

  const onResponseFail = (error: AxiosError): Promise<never> => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      onUnauthenticated();
    }
    return Promise.reject(error);
  }

  HttpService.addResponseInterceptor(onResponseSuccess, onResponseFail);
};
