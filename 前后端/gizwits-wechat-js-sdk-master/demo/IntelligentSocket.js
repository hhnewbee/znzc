/**
 * Created by new bee on 2017/5/14.
 */
$(document).ready(function () {
    var gizwitsws;
    var tfexit=false;
    var dataCheck=false;
    var productName;
    var elementDataShowDiv=$(".dataShow");
    iniEvent();
    initView();

    function initView() {
        initDatabaseShow();
    }

    function iniEvent() {
        $("#initDevice").on("click", function () {
            initDevice();
        });
        $("#readData").on("click", function () {
            read();
        });
        $("#clear").on("click",function () {
            clearLog();
        });
        $("#write").on("click",function () {
            writeCommand($(this));
        });
        $(".selector").change(function () {
            writeCommand($(this));
        });

        $("#databutton").on("click",function(){
            if(dataCheck){
                dataCheck=false;
                $("#datatable").css({opacity:0});
            }else{
                dataCheck=true;
                productName=$("#dataname").val();
                $("#datatable").css({opacity:1});
            }
        });
    }

    function initDatabaseShow(data) {
    }

    function initDataDiv(data,i) {
        tfexit=true;
        elementDataShowDiv.eq(i).append("<div class='dataIten'>"+data+"</div>");
    }
//初始化sdk
    function newObj() {
        if (gizwitsws != null) {
            alert("对象已被初始化，如需改变参数，请刷新页面.");
            return;
        }
        var apiHost = "api.gizwits.com";
        var commType = "attrs_v4";
        var wechatOpenId = "jkjkjkjkjk";
        var gizwitsAppId = "361d40a375e643058bdecae98a671613";
        gizwitsws = new GizwitsWS(apiHost, wechatOpenId, gizwitsAppId, commType);

        gizwitsws.onInit = onInit;
        gizwitsws.onConnected = onConnected;
        gizwitsws.onOnlineStatusChanged = onOnlineStatusChanged;
        gizwitsws.onReceivedRaw = onReceivedRaw;
        gizwitsws.onReceivedAttrs = onReceivedAttrs;
        gizwitsws.onError = onError;

        showScreen("初始化对象成功!");
    }

//初始化sdk
    function init() {
        gizwitsws.init();
        conndids = [];
        showScreen("已发送init指令!");
    }

//获取设备标识并且连接
    function connect() {
        var did = $('#did').val();
        gizwitsws.connect(did);
        showScreen("已发送connect指令!");
    }

//开启设备
    function initDevice() {
        newObj();
        init();
        setTimeout(connect,500);
    }

    function read() {
        var did = $('#readDid').val();
        var names = $('#names').val();
        if (names == null) {
            gizwitsws.read(did, null);
        } else {
            gizwitsws.read(did, JSON.parse(names));
        }
        showScreen("已发送read指令!");
    }

    function writeCommand(id) {
        var did = $('#writeDid').val();
        var attrs;
        if(id.attr("id")=="write"){
            attrs=$("#command").val();
        }else {
            var value = id.val();
            var key = id.attr("name");
            attrs = "{\"" + key + "\"" + ":" + value + "}";
        }
        if (1) {
            try {
                gizwitsws.write(did, JSON.parse(attrs));
                showScreen("已对设备" + did + "发送write指令: " + attrs);
            } catch (e) {
                showError("数据格式错误：" + e);
            }
        } else {
            var raw = $('#command').val();
            try {
                gizwitsws.send(did, JSON.parse(raw));
                showScreen("已对设备" + did + "发送raw指令: " + raw);
            } catch (e) {
                showError("数据格式错误：" + e);
            }

        }
    }

    function clearLog() {
        $('#log').text("");
    }

//=========================================================
// callback functions
//=========================================================
    function onInit(devices) {
        if (devices.length == 0) {
            showScreen("没有绑定的设备");
        } else {
            for (var i = 0; i < devices.length; i++) {
                showScreen("==================================================");
                showScreen("已绑定设备，did=" + devices[i].did);
                showScreen("已绑定设备，mac=" + devices[i].mac);
                showScreen("已绑定设备，product_key=" + devices[i].product_key);
                showScreen("已绑定设备，is_online=" + devices[i].is_online);
                showScreen("已绑定设备, dev_alias=" + devices[i].dev_alias);
                showScreen("已绑定设备，remark=" + devices[i].remark);

                addSelectOption('#did', devices[i].did, devices[i].did);
            }
        }
    }

    function onConnected(did) {
        addSelectOption('#readDid', did, did);
        addSelectOption('#writeDid', did, did);
        showScreen("与设备:" + did + "连接成功!");
    }

    function onOnlineStatusChanged(value) {
        showScreen("设备上下线通知，did=" + value.did);
        showScreen("设备上下线通知，is_online=" + value.is_online);
    }

    function onReceivedRaw(value) {
        var str = "收到设备" + value.did + "的Raw: [";
        for (var i = 0; i < value.raw.length; i++) {
            str = str + value.raw[i] + ",";
        }
        str = str.substr(0, str.length - 1) + "]";
        showScreen(str);
    }

    function onReceivedAttrs(value) {
        var datas=[];
        var i=0;
        var str = "收到设备" + value.did + "的Attrs: ";
        if(tfexit){
            $(".dataIten").remove();
        }
        if(dataCheck){
            datas.push(productName);
            datas.push(getTime());
            $("#datatable").append("<div class='tableitem'>"+productName+"</div>");
            $("#datatable").append("<div class='tableitem'>"+getTime()+"</div>");
        }
        for (var key in value.attrs) {
            initDataDiv(value.attrs[key],i);
            //str = str + key + ":" + value.attrs[key] + "; ";
            if(dataCheck){
                if(i>6&&i<13){
                    datas.push(value.attrs[key]);
                    $("#datatable").append("<div class='tableitem'>"+value.attrs[key]+"</div>");

                }
            }
            i++;
        }

        if(dataCheck){
            var content={data:datas,type:"add"};
            sendData("http://192.168.43.189:8090/znzc/intelligentSocketPHP/DataShowADD.php"
                ,content);
        }
        showScreen(str);
    }

    function onError(value) {
        showError(value.toString());
    }

//=========================================================
// inner functions
//=========================================================
    function showScreen(txt) {
        $('#log').prepend('<p style="color: blue">' + txt + '</p>');
    }

    function showError(txt) {
        $('#log').prepend('<p style="color: red">' + txt + '</p>');
    }

    function addSelectOption(selectId, value, text) {
        if ($(selectId + ' option[value =' + value + ']').length == 0) {
            $(selectId).append("<option value=" + value + ">" + text + "</option>");
        }
    }

/////////////////////dataAddShow
//    function dataAdd(value){
//        var content=
//    }
    function getTime(){
        var date = new Date();
        var time=date.getHours() + ":" + date.getMinutes() + ":"+ date.getSeconds();
        return time;
    }
    function sendData(url,content){
        $.ajax({
            type: 'post',
            url: url,
            cache: false,
            data:content,
            success: function (data) {
                console.log(data);
            }
        });
    }
});