package it.uniba.di.sms.carpooling;

public class URLs {
    private static final String ROOT_URL = "https://carpooling2019.altervista.org/Api.php?apicall=";
    private static final String ROOT_URL_MAPS = "https://carpooling2019.altervista.org/Api_maps.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN= ROOT_URL + "login";
    public static final String URL_GETCOMPANIES= ROOT_URL + "getCompanies";
    public static final String URL_CHECKUSER= ROOT_URL + "checkUser";
    public static final String URL_GET_CARS = ROOT_URL + "getAuto";
    public static final String URL_ADD_RIDE = ROOT_URL + "addPassaggio";
    public static final String URL_ADD_CAR = ROOT_URL + "addAuto";
    public static final String URL_GETPASSAGGI= ROOT_URL + "getPassaggio";
    public static final String URL_REQUEST_RIDE = ROOT_URL + "requestPassaggio";
    public static final String URL_GET_LIST_REQUESTED_RIDES = ROOT_URL + "getListRequestedPassaggio";
    public static final String URL_GET_OFFERED_RIDES = ROOT_URL + "getListOfferedPassaggio";
    public static final String URL_GET_LIST_USER_REQUESTED = ROOT_URL + "getListUserRequested";
    public static final String URL_DELETE_CAR = ROOT_URL + "deleteAuto";
    public static final String URL_GET_PROFILE_PICTURE = ROOT_URL + "getProfilePicture";
    public static final String URL_ACCEPT_DECLINE_RIDE = ROOT_URL + "acceptDeclinePassaggio";
    public static final String URL_DELETE_RIDES = ROOT_URL + "deletePassaggi";
    public static final String URL_DELETE_REQUESTED_RIDES = ROOT_URL + "deletePassaggiRichiesti";
    public static final String URL_UPDATE_TOKEN = ROOT_URL + "updateToken";

    public static final String URL_CHECK_PASSAGGIO = ROOT_URL_MAPS + "checkPassaggio";
    public static final String URL_START_TRACKING = ROOT_URL_MAPS + "startTrackingService";
    public static final String URL_CANCEL_TRACKING = ROOT_URL_MAPS + "cancelTracking";
    public static final String URL_GET_SCORE = ROOT_URL_MAPS + "getScore";

}
