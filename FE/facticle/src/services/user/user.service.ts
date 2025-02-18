import HttpService from "../htttp.service";

class UserService {
    getUserProfile = async () => {
        const userProfileEndpoint = '/users/profile';
        return await HttpService.get(userProfileEndpoint);
    }
}

export default new UserService();