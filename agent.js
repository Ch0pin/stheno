'use strict';


let printBacktrace=function (){
  Java.perform(function() {
      let android_util_Log = Java.use('android.util.Log'), java_lang_Exception = Java.use('java.lang.Exception');
      let exc = android_util_Log.getStackTraceString(java_lang_Exception.$new());
      colorLog(exc, { c: Color.Green });
  });
};

function dumpIntent(intent){
    try{
        if(!intent.hasExtra("marked_as_dumped")){
            sendIntentToMonitor(intent)
            intent.putExtra("marked_as_dumped","marked");
        }
    } catch(error){
        console.log(error);
    }
}

function sendIntentToMonitor(intent){
    try{
        let bundle_clz = intent.getExtras();
        let data = intent.getData();
        let action = intent.getAction();
        let flags = intent.getFlags();
        let exported = isActivityExported(intent);
        let component  = intent.getComponent();
        let itype = intent.getType();

        let targetPackage = "";
        let targetClassName = "";    
        let dataToString ="";
        let extras = "";
        let type = "";
    
        if(data != null){
            dataToString = data.toString();
        }
        
        if(component != null){
            targetPackage = component.getPackageName();
            targetClassName = component.getClassName();
        }

        if(itype != null){
            type = itype.toString();
        }

        if(bundle_clz != null){
            let keySet = bundle_clz.keySet();
            let iter = keySet.iterator();
            while(iter.hasNext()) {
                let currentKey = iter.next();
                let currentValue = bundle_clz.get(currentKey);
                if (currentValue!=null)
                    type =  currentValue.getClass().toString();
                else type = 'undefined'
                let t = type.substring(type.lastIndexOf('.')+1,type.length)
                if(currentKey!='marked_as_dumped'){
                    extras += '\t('+t+ ') '+ currentKey + ' = ' + currentValue+'\n'
                }
            }
            extras+='\n\n'
        }
      
        let sentData = JSON.stringify({"description":intent.toString(), "targetPackageName":targetPackage, 
            "targetClassName":targetClassName, "action":action, "data":dataToString, "type":type, "flags":flags, 
            "extras":extras, "targetIsExported":exported});

          send('IntentMsg|'+sentData);

    } catch(e){
        throw e;
    }
}

function getApplicationContext(){
      return Java.use('android.app.ActivityThread').currentApplication().getApplicationContext();
}

function isActivityExported(intent){
    try{
        const context = getApplicationContext();
        const packageManager = context.getPackageManager();  
        let resolveInfo = packageManager.resolveActivity(intent, 0);
        if(resolveInfo != null)
            return resolveInfo.activityInfo.value.exported.value;
        else return null;
    } catch(error){
        console.log(error)
    }
}

Java.perform(function() { 

    let activity = Java.use('android.app.Activity');

    activity.startActivity.overload('android.content.Intent', 'android.os.Bundle').implementation = function(intent, bundle){
        dumpIntent(intent);   
        this.startActivity(intent, bundle);
    }

    activity.startActivities.overload('[Landroid.content.Intent;', 'android.os.Bundle').implementation = function(intents, bundle){
        for (let i = 0; i < intents.length; i++) {
            dumpIntent(intents[i]);  
        }
        this.startActivities(intents, bundle);
    }


    activity.startActivityForResult.overload('android.content.Intent', 'int', 'android.os.Bundle').implementation = function(intent, requestCode, bundle){
        if( requestCode != -1){
            dumpIntent(intent);
        }
        this.startActivityForResult(intent,requestCode, bundle);
    }

    activity.startActivityIfNeeded.overload('android.content.Intent', 'int', 'android.os.Bundle').implementation = function(intent, requestCode, bundle){
        dumpIntent(intent);
        this.startActivityIfNeeded(intent, requestCode, bundle);
    }

    activity.setResult.overload('int','android.content.Intent').implementation = function(requestCode,intent){
        dumpIntent(intent);
        this.setResult(requestCode,intent);
    }


    let hook_1703927890 = Java.use('android.content.Intent');

    let interestingMethodNames = [
        'getBundleExtra',
        'hasExtra',
        'getAction',
        'getClipData',
        'getDataString',
        'getData',
        'getBooleanArrayExtra',
        'getBooleanExtra',
        'getByteArrayExtra',
        'getByteExtra',
        'getCharArrayExtra',
        'getCharExtra',
        'getCharSequenceArrayExtra',
        'getCharSequenceArrayListExtra',
        'getCharSequenceExtra',
        'getDoubleArrayExtra',
        'getDoubleExtra',
        'getFloatArrayExtra',
        'getFloatExtra',
        'getIntArrayExtra',
        'getIntExtra',
        'getIntegerArrayListExtra',
        'getLongArrayExtra',
        'getShortArrayExtra',
        'getShortExtra',
        'getStringArrayExtra',
        'getStringArrayListExtra',
        'getStringExtra',
        'getExtras',
        'getSerializableExtra',
        'getParcelableExtra',
        'getParcelableArrayListExtra',
        'getParcelableArrayExtra'
    ];

    let isDumping = false;

    function hookIntentGetMethod(methodName) {
        try {
            if (hook_1703927890[methodName]) {  // Ensure the method exists
                let overloadCount_1703927890 = hook_1703927890[methodName].overloads.length;
                for (let i = 0; i < overloadCount_1703927890; i++) {
                    hook_1703927890[methodName].overloads[i].implementation = function() {
                        let result = this[methodName].apply(this, arguments);
                        // Avoid re-entrancy
                        if (!isDumping && !this.hasExtra("marked_as_dumped")) {
                            try {
                                isDumping = true;
                                dumpIntent(this);
                                this.putExtra("marked_as_dumped", "marked");
                            } catch (copyError) {
                                console.log("Error creating or dumping Intent copy: " + copyError);
                            } finally {
                                isDumping = false;
                            }
                        } 
                        return result;
                    };
                }
            } else {
                console.log('Method ' + methodName + ' does not exist on android.content.Intent');
            }
        } catch (error) {
            console.log("Error in hookIntentGetMethod: " + error);
        }
    }
    

    for (var i = 0; i < interestingMethodNames.length; i++) {
        var methodName = interestingMethodNames[i];
        hookIntentGetMethod(methodName);
    }

});

