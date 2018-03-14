/**
 * jquery.dialogbox v1.0
 * Copyright (C) 2015 Afraware Technology Inc.
 * Licensed under the MIT license.
 * Created by Milad Rahimi [http://miladrahimi.com] on 20/2/2015.
 * http://www.sucaijiayuan.com
 */
(function ($) {
    $.fn.dialogbox = function ($options, $callback) {
        var $id = '#dialogbox_1';
        var $DOM_ID = $id.substr(1);
        this.append(
            '<div id="' + $DOM_ID + '">' +
            '<div class="divDialogboxBack">' +
            '<div class="divDialogboxMain">' +
            '<div class="divDialogboxTitle"></div>' +
            '<div class="divDialogboxButtonBar"></div>' +
            '</div>' + '</div>' + '</div>');
        var $settings = $.extend({
            type: 'normal',
            title: 'Message'
        }, $options);
        // Draw Basic
        switch($settings['type']) {
            case 'ok/cancel':
             $($id + ' .divDialogboxButtonBar')
                .append('<div class="divDialogboxBtn" id="divDialogOk">OK</div>')
                .append('<div class="divDialogboxBtn" id="divDialogCancel">Cancel</div>');
                break;
        }
        $($id + ' .divDialogboxBack').css({
            "display":"flex",
            "justify-content":"center",
            "align-items":"center"
        });
        $($id + ' .divDialogboxTitle').html($settings['title']);
        $($id + ' .divDialogboxBack').fadeIn('fast');
        $($id + ' .divDialogboxMain').fadeIn('fast');
        //这是$id的后代元素，注意别忘了空格
        $($id +' #divDialogCancel').on("click",function () {
            $($id).fadeOut(200);
            $($id).remove();
        });
        $($id +' #divDialogOk').on("click",function () {
            $($id).fadeOut(200);
            $callback();
            $($id).remove();
        });
    };
}(jQuery));