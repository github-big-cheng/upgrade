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
    },


    /**
     * 值转换
     * @param mapKey
     * @param val
     * @returns {*}
     */
    dictTypeConvert: function (mapKey, val) {
        let map = dictMap[mapKey];
        return map == null ? "" : (map[val] == null ? "" : map[val]);
    },


    /**
     * 排序
     * @param arr 要排序的数组
     * @param _Comparator 比较器
     */
    sort : function(arr, _Comparator){
        _doSort(arr, 0, arr.length-1, [], _Comparator);

        function _doSort(arr, left, right, temp, _Comparator){
            if(left < right){
                let mid = parseInt((left + right) / 2);
                _doSort(arr, left, mid, temp, _Comparator); // 左子序排序
                _doSort(arr, mid+1, right, temp, _Comparator); // 右子序排序
                _merge(arr, left, mid, right, temp, _Comparator); // 合并
            }
        };
        function _merge(arr, left, mid, right, temp, _Comparator){
            let i = left; // 左序列指针
            let j = mid + 1; // 右序列指针
            let k = 0; // 临时下标

            // 比较左右序列大小，将小的放入临时数据中
            while(i<=mid && j<=right){
                if(_Comparator(arr[i], arr[j]) > 0){
                    temp[k++] = arr[j++];
                }else{
                    temp[k++] = arr[i++];
                }
            }

            // 将左序列剩余元素放入临时数组
            while(i <= mid){
                temp[k++] = arr[i++];
            }

            // 将右序列生于元素放入临时数组
            while(j <= right){
                temp[k++] = arr[j++];
            }

            // 排序后数组复制回原数组
            k = 0;
            while(left <= right){
                arr[left++] = temp[k++];
            }
        };
    },


};