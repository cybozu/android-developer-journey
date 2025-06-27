import com.android.build.api.dsl.LibraryExtension
import com.cybozu.sample.buildLogic.convention.android
import com.cybozu.sample.buildLogic.convention.configureAndroidCommon
import com.cybozu.sample.buildLogic.convention.configureKotlinCommon
import com.cybozu.sample.buildLogic.convention.getPluginId
import com.cybozu.sample.buildLogic.convention.kotlinAndroid
import com.cybozu.sample.buildLogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("android-library"))
                apply(libs.getPluginId("kotlin-android"))
            }

            kotlinAndroid {
                configureKotlinCommon(this)
            }

            android<LibraryExtension> {
                configureAndroidCommon()
            }
        }
    }
}
