package com.safshekan.parkban.persistence.models;

public enum ResponseResultType {

    NotSet(0,"تنظیم نشده"),
    Ok(1,"موفق"),
    Fail(2,"ناموفق"),
    Retry(3,"سعی مجدد"),
    TimeOut(4,"وقفه"),
    CanNotSave(5,"ثبت ناموفق"),
    CanNotLoad(6,"بارگذاری با موفقیت انجام نشد"),
    ValidationFailed(7,"تایید نشده"),
    CanNotDelete(8,"امکان حذف به دلیل استفاده شدن وجود ندارد"),
    InvalidUserNameOrPassword(10,"نام کاربری و یا رمز عبور اشتباه می باشد"),
    InvalidToken(11,"علامت مشخصه درست نمی باشد"),
    ExpireToken(12,"علامت مشخصه منقضی شده است"),
    LogoutSuccess(13,"خروج از سیستم با موفقیت انجام شد"),
    LogoutFail(14,"خروج از سیستم با موفقیت انجام شد"),
    CanNotModify(15,"امکان ویرایش اطلاعات وجود ندارد"),
    CanNotFindPermission(16,"برای کاربر فوق دسترسی تعریف نشده است"),
    CanNotFindUser(17,"کاربر یافت نشد"),
    ErrorOnHttpPost(18,"خطا در ارسال اطلاعات به سرویس دهنده"),
    ErrorOnHttpGet(19,"خطا در دریافت اطلاعات از سرویس دهنده"),
    UserIsDeactive(20,"کاربر غیر فعال می باشد"),
    DuplicateData(21,"اطلاعات ورودی تکراری می باشد"),
    CanNotPermissionToService(22,"عدم دسترسی مجاز به سرویس دهنده"),
    UnauthorizedUser(23,"کاربر غیر مجاز"),
    CreditIsNotEnough(24,"اعتبار شما کافی نمی باشد"),
    LockOfMobkartHaveError(25,"بازبینی قفل با خطا مواجه شد"),
    RetrofitError(100, "خطای ارتباط"),
    NotFoundEntry(201,"ورود یافت نشد"),
    ServerError(101, "خطای سرور");

    private final int value;
    private final String description ;

    ResponseResultType(int value , String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
