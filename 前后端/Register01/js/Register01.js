/**
 * Created by new bee on 2017/3/30.
 */
$(document).ready(function() {
    var eForm=$("#form");
    var eInputPhoneNumber=$("#inputName");
    var eInputPassword=$("#inputPassword");
    var eInputPasswordConfirm=$("#inputPasswordConfirm");
    var eDiv_InputTextPasswordConfirm=$("#div_inputTextPasswordConfirm");
    var eSpanVerificationCode=$("#spanVerificationCode");
    var eDiv_InputTextVerificationCode=$("#div_inputTextVerificationCode")
    var sOriginalPassword=null;
    var bNameRight=false;
    var bPasswordRight=false;
    var bPasswordConfirmRight=true;

    finputTextBlur("#inputName", "#spanName");
    finputTextBlur("#inputPassword", "#spanPassword");
    finputTextBlur("#inputPasswordConfirm", "#spanPasswordConfirm");
    finputTextFocus("#inputName", "#spanName");
    finputTextFocus("#inputPassword", "#spanPassword");
    finputTextFocus("#inputPasswordConfirm", "#spanPasswordConfirm");
    fContentHadDialog();
    fPasswordConfirmConfirmBlur();
    fConfirm();
    fVerificationCode();

    function finputTextBlur(eInput, eSpan) {
        $(eInput).blur(function () {
            if ($(eInput).val() != "") {
                $(eSpan).css({
                    "opacity": "1",
                    "top": "10" + "px"
                }, 400);
                $(eInput).css({
                    "padding-top": "28" + "px",
                    "padding-bottom": "12px"
                }, 400);
            } else {
                $(eSpan).animate({
                    "opacity": "0",
                    "top": "0" + "px"
                }, 400);
                $(eInput).animate({
                    "padding-top": "20" + "px",
                    "padding-bottom": "20px"
                }, 400);
            }
        });
    }

    function finputTextFocus(eInput, eSpan) {
        $(eInput).focus(function () {
            $(eSpan).animate({
                "opacity": "1",
                "top": "10" + "px"
            }, 400);
            $(eInput).animate({
                "padding-top": "28" + "px",
                "padding-bottom": "12px"
            }, 400);
        })
    }

    function fLogin() {
        eDiv_InputTextVerificationCode.fadeIn(200);
        $("#div_inputTextPasswordConfirm").fadeIn(200);
        $("#labelLogin").css({color: "#FF7975"});
        $("#labelRegister").css({color: "white"});
        $("#spanPasswordConfirm").css({
            "opacity": "0",
            "top": "0" + "px"
        });
        $("#inputPasswordConfirm").css({
            "padding-top": "20" + "px",
            "padding-bottom": "20px"
        });
    }

    function fRegister() {
        eDiv_InputTextVerificationCode.fadeOut(200);
        $("#div_inputTextPasswordConfirm").fadeOut(200);
        $("#labelRegister").css({color: "#FF7975"});
        $("#labelLogin").css({color: "white"});
        $("#inputPasswordConfirm").val("");
        fFormErrorElement(true,'spanPasswordConfirmError',"输入密码不一致",$("#div__inputTextPasswordConfirm"));
    }

    function fContentHadDialog() {
        var sId;
        var func;
        $(".labelLR").click(function(e) {
            sId=$(e.target).attr("id");
            var content={password:eInputPassword.val(),type:"a"};
            // fIfSomeThing();
            fFormSubmit("http://192.168.43.189:8090/znzc/intelligentSocketPHP/User.php",content);
        });

        function fIfSomeThing() {
            var eInput=$(".div_inputText").find("input");
            if(sId=="labelLogin"){
                //函数后面加括号的话是将执行的结果进行返回。
                func=fLogin;
            }else if(sId=="labelRegister"){
                func=fRegister;
            }
            if(!fIfPrepareCommit()){
                var bTF=false;
                $.each(eInput,function()
                {
                    if($(this).val()){
                        $('body').dialogbox({type:"ok/cancel",title:"OK Cancel Question"},func);
                        bTF=true;
                        return false;
                    }
                });
                if(!bTF){
                    func();
                }
            }
        }

        function fIfPrepareCommit() {
            if($("#"+sId).css("color")=="rgb(255, 121, 117)"){
                // setTimeout(fFormSubmit('http://localhost:8090/webchat1.1/ConfirmAjaxText/cat01.php'),10);
                return true;
            }
        }
    }

    function fConfirm() {
        fPhoneNumberConfirm();
        fPasswordConfirm();
        fPasswordConfirmConfirm();
    }

    function fPhoneNumberConfirm() {
        fPhoneNumberConfirmInput();
        eInputPhoneNumber.blur(function () {
            var tf=fPhoneNumberConfirmRg();
            fFormErrorElement(tf,"spanPhoneError","请输入正确的手机号",$("#div_inputTextName"));
            bNameRight=tf;
        });
        function fPhoneNumberConfirmRg() {
            var pattern = /^1\d{10}$/;
            return pattern.test(eInputPhoneNumber.val());
        }

	    function fPhoneNumberConfirmRg2() {
	        var tf;
            var iNumberLength=eInputPhoneNumber.val().length;
	        var pattern = /^1\d+$|^1$/;
	        tf= pattern.test(eInputPhoneNumber.val());
	        if(tf){
	            if(iNumberLength>11){
	                tf=false;
	            }
	        }
	        return tf;
	    }

        function fPhoneNumberConfirmInput() {
            eInputPhoneNumber.on("input",function () {
                var content=eInputPhoneNumber.val();
                if(content.length==11){
                    var newContent={phone:content,type:"i"};
                    fAjaxIndividualJudge("http://192.168.43.189:8090/znzc/intelligentSocketPHP/User.php",newContent);
                }
                fFormErrorElement(fPhoneNumberConfirmRg2(),"spanPhoneError","请输入正确的手机号",$("#div_inputTextName"));
            });
        }
    }

    function  fPasswordConfirm() {
        fPasswordConfirmInput();
        eInputPassword.blur(function () {
            var sPasswordContent=eInputPassword.val();
            var tf=fPasswordConfirmRg(sPasswordContent);
            sOriginalPassword=sPasswordContent;
            fFormErrorElement(tf,"spanPasswordError","请不要输入空格",$("#div_inputTextPassword"));
            bPasswordRight=tf;
        });
        function fPasswordConfirmRg(){
            var pattern = /^\S+$/;
            return pattern.test(eInputPassword.val());
        }

        function fPasswordConfirmInput() {
            eInputPassword.on("input",function () {
                fFormErrorElement(fPasswordConfirmRg(),"spanPasswordError","请输入正确的手机号",$("#div_inputTextPassword"));
            });
        }
    }

    function fPasswordConfirmConfirm() {
        eInputPasswordConfirm.on("input",function () {
            var sPasswordConfirmContent=eInputPasswordConfirm.val();
            var iConfirmPasswordLength= sPasswordConfirmContent.length;
            var tf=false;
            if(sOriginalPassword&&sPasswordConfirmContent){
                var sSubstringOriginaPassword=sOriginalPassword.substring(0,iConfirmPasswordLength);
                tf=sPasswordConfirmContent==sSubstringOriginaPassword;
            }
            fFormErrorElement(tf,'spanPasswordConfirmError',"输入密码不一致",$("#div_inputTextPasswordConfirm"));
            bPasswordConfirmRight=tf;
        });
    }

    function fPasswordConfirmConfirmBlur() {
        eInputPasswordConfirm.on("blur",function () {
            bPasswordConfirmRight=false;
            var sPasswordConfirmContent=eInputPasswordConfirm.val();
            if(sOriginalPassword&&sPasswordConfirmContent){
                bPasswordConfirmRight=sOriginalPassword==sPasswordConfirmContent;
                if(!bPasswordConfirmRight){
                    fFormErrorElement(bPasswordConfirmRight,'spanPasswordConfirmError',"输入密码不一致",$("#div_inputTextPasswordConfirm"));
                }
            }
        });
        return bPasswordConfirmRight;
    }

    function fWhetherPasswordConfirmConfirm() {
        var tf=false;
        if($("#div_inputTextPasswordConfirm").css("display")=="none"){
            tf=true;
        }
        return tf;
    }

    function fFormErrorElement(tf,id,text,element) {
        if(!tf){
            if($("#"+id).length>0){
                $("span").remove("#"+id);
                element.after("<span style='color:red' id="+id+">"+text+"</span>");
            }else{
                element.after("<span style='color:red' id="+id+">"+text+"</span>");
            }
        }else{
            $("span").remove("#"+id);
        }
    }

    function fFormErrorElementRight(tf,id,text,element) {
        if(tf=="1"){
            if($("#"+id).length>0) return;
            element.after("<span style='color:green' id="+id+">"+text+"</span>");
        }else{
            element.after("<span style='color:red' id="+id+">"+"账号不存在"+"</span>");
        }
    }

    function fFormSubmit(url,content) {
        if(fWhetherPasswordConfirmConfirm())
            bPasswordConfirmRight=true;
        if (bNameRight && bPasswordConfirmRight && bPasswordRight) {
            fAjaxteForm();
        }else {
            if (!bNameRight) {
                fFormErrorElement(bNameRight, "spanPhoneError", "请输入正确的手机号", $("#div_inputTextName"));
            }
            if (!bPasswordRight) {
                fFormErrorElement(bPasswordRight, "spanPasswordError", "请不要输入空格", $("#div_inputTextPassword"));
            }
            if (!bPasswordConfirmRight) {
                fFormErrorElement(bPasswordConfirmRight, 'spanPasswordConfirmError', "输入密码不一致", $("#div_inputTextPasswordConfirm"));
            }
        }

        function fAjaxteForm() {
            $.ajax({
                type: 'post',
                url: url,
                // data: eForm.serialize(),
                data:content,
                cache: false,
                success: function (data) {
                    if(data==0){
                        fFormErrorElement(0, "spanPasswordError", "密码错误", $("#div_inputTextPassword"));
                    }else{
                        window.location.href=data;
                    }
                    console.log(data);
                }
            });
        }
    }

    function fAjaxIndividualJudge(url,content) {
       $.ajax({
           type: 'post',
           url: url,
           data: content,
           cache: false,
           success: function (data) {
               console.log(data);
               fFormErrorElementRight(data,"spanPhoneError","账号正确",$("#div_inputTextName"));
           }
       });
   }
    function fVerificationCode() {
        var n=0;
        eSpanVerificationCode.on("click",function () {
            var timer=setInterval(function () {
                if(timer==60){
                    clearInterval(timer);
                }
                n++;
                eSpanVerificationCode.text(toDub(n));
            },1000);
        });
    }

    function toDub(n){
        return n<10?"0"+n:""+n;
    }

    function fAjaxteForm() {
        $.ajax({
            type: 'post',
            url: url,
            data: eForm.serialize(),
            // data:content,
            cache: false,
            success: function (data) {
                console.log(data);
            }
        });
    }
});