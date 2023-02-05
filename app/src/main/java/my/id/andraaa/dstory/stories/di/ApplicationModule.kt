package my.id.andraaa.dstory.stories.di

//import okhttp3.logging.HttpLoggingInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import my.id.andraaa.dstory.stories.data.AuthDataSource
import my.id.andraaa.dstory.stories.data.DicodingStoryDataSource
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.presentor.add_story.AddStoryViewModel
import my.id.andraaa.dstory.stories.presentor.auth.signin.SignInViewModel
import my.id.andraaa.dstory.stories.presentor.auth.signup.SignUpViewModel
import my.id.andraaa.dstory.stories.presentor.main.MainViewModel
import my.id.andraaa.dstory.stories.presentor.main.stories.StoriesViewModel
import my.id.andraaa.dstory.stories.presentor.story.StoryViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create


val ApplicationModule = module {
    single { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }

    single {
//        val logging = HttpLoggingInterceptor()
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
//            .addInterceptor(logging)
            .addInterceptor { chain ->
                var requestBuilder = chain.request().newBuilder()
                val session = runBlocking { get<AuthDataSource>().getSession() }
                session?.token?.let { token ->
                    requestBuilder = requestBuilder
                        .addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    single {
        Retrofit.Builder().baseUrl(DicodingStoryService.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
            .create<DicodingStoryService>()
    }

    single { AuthDataSource(androidContext(), get(), get()) }

    single { DicodingStoryDataSource(get()) }

    viewModelOf(::MainViewModel)
    viewModelOf(::AddStoryViewModel)
    viewModelOf(::StoryViewModel)
    viewModelOf(::StoriesViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
}