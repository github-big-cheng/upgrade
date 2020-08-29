const CommonJS = {
    /**
     * 字典查询
     * @param type
     * @param _cb
     */
    dictQuery : function(type, _cb){
        $.ajax({
            type: 'post',
            dataType: 'json',
            async : false,
            data : {'type' : type},
            url: '/common/dictList.json',
            success: function (rdata) {
                // console.log(rdata);
                if(rdata && rdata.code=='0'){
                    _cb(rdata.data);
                }
            }
        });
    },


    /**
     * 模板填充回调
     * @param rdata
     * @param target
     * @param tmpl
     * @param name
     */
    callback : function(rdata, target, tmpl, name){

        if(!rdata){
            return;
        }

        let arr = [];
        for(let i in rdata){
            arr.push({value:i,text:rdata[i]});
        }
        // console.log(arr);

        $("#"+tmpl).tmpl(arr, {
            getName : function(){
                return name;
            }
        }).appendTo($("#"+target));
    }

};