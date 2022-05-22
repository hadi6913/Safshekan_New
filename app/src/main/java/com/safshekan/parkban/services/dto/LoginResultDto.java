package com.safshekan.parkban.services.dto;



import com.google.gson.annotations.SerializedName;

public class LoginResultDto extends ResultDto {

    @SerializedName("Values")
    private Parkban value;

    public Parkban getValue() {
        return value;
    }

    public void setValue(Parkban value) {
        this.value = value;
    }

    public static class Parkban{

        @SerializedName("UserName")
        private String UserName;
        @SerializedName("UserPass")
        private String UserPass;
        @SerializedName("IsActive")
        private boolean isActive;
        @SerializedName("WalletCashAmount")
        private long walletCashAmount;
        @SerializedName("FirstName")
        private String FirstName;
        @SerializedName("LastName")
        private String LastName;
        @SerializedName("FatherName")
        private String FatherName;
        @SerializedName("TellNumber")
        private long tellNumber;
        @SerializedName("PhonNumber")
        private long phoneNumber;
        @SerializedName("UserToken")
        private String userToken;
        @SerializedName("DoorShift")
        private long doorShift;
        @SerializedName("Shift")
        public int shift ;
        @SerializedName("ShiftToTime")
        public String shiftToTime ;
        @SerializedName("CurrentParking")
        private long currentParking;
        @SerializedName("UserType")
        public int UserType ;
        @SerializedName("UserAccessLevelId")
        private long userAccessLevelId;
        @SerializedName("NationalCode")
        private String nationalCode;
        @SerializedName("AccessPermissionValuePart1")
        private long accessPermissionValuePart1;
        @SerializedName("AccessPermissionValuePart2")
        private long accessPermissionValuePart2;
        @SerializedName("FullName")
        private String fullName;
        @SerializedName("Captcha")
        private String captcha ;
        @SerializedName("Description")
        private String description ;
        @SerializedName("Address")
        private String address ;
        @SerializedName("Id")
        private long id;
        @SerializedName("PersistOn")
        private String persistOn;


        public String getPersistOn() {
            return persistOn;
        }

        public void setPersistOn(String persistOn) {
            this.persistOn = persistOn;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        //****************************************************************


        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }
        //****************************************************************


        public String getUserPass() {
            return UserPass;
        }

        public void setUserPass(String userPass) {
            UserPass = userPass;
        }
        //****************************************************************


        public String getFirstName() {
            return FirstName;
        }

        public void setFirstName(String firstName) {
            FirstName = firstName;
        }
        //****************************************************************


        public String getLastName() {
            return LastName;
        }

        public void setLastName(String lastName) {
            LastName = lastName;
        }
        //****************************************************************


        public String getFatherName() {
            return FatherName;
        }

        public void setFatherName(String fatherName) {
            FatherName = fatherName;
        }
        //****************************************************************


        public long getTellNumber() {
            return tellNumber;
        }

        public void setTellNumber(long tellNumber) {
            this.tellNumber = tellNumber;
        }
        //****************************************************************


        public long getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(long phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        //****************************************************************

        public long getUserAccessLevelId() {
            return userAccessLevelId;
        }

        public void setUserAccessLevelId(long userAccessLevelId) {
            this.userAccessLevelId = userAccessLevelId;
        }
        //****************************************************************


        public String getNationalCode() {
            return nationalCode;
        }

        public void setNationalCode(String nationalCode) {
            this.nationalCode = nationalCode;
        }
        //****************************************************************


        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }

        //****************************************************************


        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        //****************************************************************


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        //****************************************************************


        public long getAccessPermissionValuePart1() {
            return accessPermissionValuePart1;
        }

        public void setAccessPermissionValuePart1(long accessPermissionValuePart1) {
            this.accessPermissionValuePart1 = accessPermissionValuePart1;
        }

        //****************************************************************


        public long getAccessPermissionValuePart2() {
            return accessPermissionValuePart2;
        }

        public void setAccessPermissionValuePart2(long accessPermissionValuePart2) {
            this.accessPermissionValuePart2 = accessPermissionValuePart2;
        }

        //****************************************************************


        public long getDoorShift() {
            return doorShift;
        }

        public void setDoorShift(long doorShift) {
            this.doorShift = doorShift;
        }

        //****************************************************************


        public long getCurrentParking() {
            return currentParking;
        }

        public void setCurrentParking(long currentParking) {
            this.currentParking = currentParking;
        }

        //****************************************************************


        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }
        //****************************************************************


        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        //****************************************************************


        public long getWalletCashAmount() {
            return walletCashAmount;
        }

        public void setWalletCashAmount(long walletCashAmount) {
            this.walletCashAmount = walletCashAmount;
        }
        //****************************************************************


        public int getShift() {
            return shift;
        }

        public void setShift(int shift) {
            this.shift = shift;
        }

        //****************************************************************


        public String getShiftToTime() {
            return shiftToTime;
        }

        public void setShiftToTime(String shiftToTime) {
            this.shiftToTime = shiftToTime;
        }

        //****************************************************************


        public int getUserType() {
            return UserType;
        }

        public void setUserType(int userType) {
            UserType = userType;
        }
        //****************************************************************












    }
}
