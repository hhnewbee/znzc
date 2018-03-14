/**
 * Created by new bee on 2017/4/6.
 */
function fVerificationCode() {
    var iCount=60;
    eDiv_InputTextVerificationCode.on("click",function () {
        fCountDown();
    });
    function fCountDown() {
        iCount-=1;
        eDiv_InputTextVerificationCode.text(iCount);
        if(iCount==0){
            eDiv_InputTextVerificationCode.text("重新输入验证码");
            return;
        }
        setInterval(fCountDown(),1000);
    }
}