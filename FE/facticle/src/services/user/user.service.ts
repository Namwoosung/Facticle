import HttpService from "../htttp.service";

class UserService {
    getUserProfile = async () => {
        const userProfileEndpoint = '/users/profile';
        return await HttpService.get(userProfileEndpoint);
    }

    updateUserProfile = async (payload: { nickname: string, email: string | null }) => {
        const updateUserProfileEndpoint = '/users/profile';
        return await HttpService.patch(updateUserProfileEndpoint, payload);
    }

    uploadProfileImage = async (file: File) => {
        const uploadProfileImageEndpoint = '/users/profile-image';
        return await HttpService.upload(uploadProfileImageEndpoint, file);
    }

    deleteProfileImage = async () => {
        const deleteProfileImageEndpoint = '/users/profile-image';
        return await HttpService.delete(deleteProfileImageEndpoint);
    }
}

export default new UserService();