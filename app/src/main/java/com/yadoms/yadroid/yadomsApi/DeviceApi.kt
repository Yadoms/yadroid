package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.util.Log
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DeviceApi(private val yApi: YadomsApi) {
    private val _logTag = javaClass.canonicalName

    enum class KeywordTypes {
        NoData,
        StringType,
        Numeric,
        Bool,
        Json,
        Enum,
        DateTime
    }

    enum class StandardCapacities {
        Alarm,
        ApparentPower,
        ArmingAlarm,
        BatteryLevel,
        CameraMove,
        ColorRGB,
        ColorRGBW,
        Counter,
        Current,
        Curtain,
        DateTime,
        Debit,
        Dimmable,
        Direction,
        Distance,
        Duration,
        ElectricLoad,
        Energy,
        Event,
        Frequency,
        Humidity,
        Illumination,
        IlluminationWm2,
        Load,
        Message,
        PluginState,
        Power,
        PowerFactor,
        Pressure,
        Rain,
        RainRate,
        Rssi,
        SignalLevel,
        SignalPower,
        Speed,
        Switch,
        Tamper,
        Temperature,
        Text,
        UpDownStop,
        UserCode,
        Uv,
        Voltage,
        Volume,
        WeatherCondition,
        Weight
    }

    enum class KeywordAccess {
        NoAccess,
        Get,
        GetSet
    }

    data class Device(
        val id: Int,
        val pluginId: Int,
        val friendlyName: String
    )

    data class Keyword(
        val id: Int,
        val deviceId: Int,
        val friendlyName: String,
        val lastAcquisitionValue: String,
        val lastAcquisitionDate: LocalDateTime?,
        val accessMode: KeywordAccess,
        val type: KeywordTypes
    )

    class GetDeviceMatchKeywordCriteriaRequestAdapter(
        val expectedKeywordType: Array<KeywordTypes>,
        val expectedCapacity: Array<StandardCapacities>,
        val expectedKeywordAccess: Array<KeywordAccess>
    )

    class GetDeviceMatchKeywordCriteriaResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val devices: List<Device>, val keywords: List<Keyword>)
    }

    fun getDeviceMatchKeywordCriteria(
        context: Context?,
        expectedKeywordType: Array<KeywordTypes> = arrayOf(),
        expectedCapacity: Array<StandardCapacities> = arrayOf(),
        expectedKeywordAccess: Array<KeywordAccess> = arrayOf(),
        onOk: (List<Device>, List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val body = moshi.adapter(GetDeviceMatchKeywordCriteriaRequestAdapter::class.java)
            .toJson(GetDeviceMatchKeywordCriteriaRequestAdapter(expectedKeywordType, expectedCapacity, expectedKeywordAccess))

        yApi.post(
            context,
            url = "/device/matchkeywordcriteria",
            body = body.toString(),
            onOk = {
                try {
                    val result: GetDeviceMatchKeywordCriteriaResultAdapter? =
                        moshi.adapter(GetDeviceMatchKeywordCriteriaResultAdapter::class.java).fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.devices, result.data.keywords)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }

    class GetDeviceWithCapacityTypeResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val device: List<Device>)
    }

    fun getDeviceWithCapacityType(
        context: Context?,
        expectedKeywordType: KeywordTypes,
        expectedKeywordAccess: KeywordAccess = KeywordAccess.NoAccess,
        onOk: (List<Device>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            context,
            url = "/device/matchcapacitytype/$expectedKeywordAccess/$expectedKeywordType",
            onOk = {
                try {
                    val result: GetDeviceWithCapacityTypeResultAdapter? =
                        moshi.adapter(GetDeviceWithCapacityTypeResultAdapter::class.java).fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.device)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }

    class GetDeviceKeywordsResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val keyword: List<Keyword>)
    }

    fun getDeviceKeywords(
        context: Context?,
        deviceId: Int,
        onOk: (List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            context,
            url = "/device/$deviceId/keyword",
            onOk = {
                try {
                    val result: GetDeviceKeywordsResultAdapter? = moshi.adapter(GetDeviceKeywordsResultAdapter::class.java).fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.keyword)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }

    class GetKeywordResultAdapter(val result: Boolean, val message: String, val data: Keyword)

    fun getKeyword(
        context: Context?,
        keywordId: Int,
        onOk: (Keyword) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            context,
            url = "/device/keyword/$keywordId",
            onOk = {
                try {
                    val result: GetKeywordResultAdapter? = moshi.adapter(GetKeywordResultAdapter::class.java).fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it) :")//TODO gérer les erreurs dans la fonction get
                onError(it)
            }
        )
    }

    class CommandResultAdapter(val result: Boolean, val message: String)

    fun command(
        context: Context?,
        keywordId: Int,
        command: String,
        onOk: () -> Unit,
        onError: (String?) -> Unit
    ) {
        yApi.post(
            context,
            url = "/device/keyword/${keywordId}/command",
            body = command,
            onOk = {
                try {
                    val result: CommandResultAdapter? = moshi.adapter(CommandResultAdapter::class.java).fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk()
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it) :")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }

    internal class LocalDateTimeAdapter {
        private val pattern = "yyyyMMdd'T'HHmmss[.SSSSSS]"
        private val notADateTimeSpecialValue = "not-a-date-time"

        @ToJson
        fun toJson(dt: LocalDateTime?): String? = when (dt) {
            null -> notADateTimeSpecialValue
            else -> dt.format(DateTimeFormatter.ofPattern(pattern))
        }

        @FromJson
        fun fromJson(dt: String): LocalDateTime? = when (dt) {
            notADateTimeSpecialValue -> null
            else -> LocalDateTime.parse(dt, DateTimeFormatter.ofPattern(pattern))
        }
    }

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .build()
    }
}