package com.gtech.narisafety.builder

import android.content.Context
import com.gtech.narisafety.enums.AlarmType
import com.gtech.narisafety.interfaces.IAlarmListener


internal class Alarm(var context: Context?, var id: String?, var time: Long, var alarmType: AlarmType?, var alarmListener: IAlarmListener?)