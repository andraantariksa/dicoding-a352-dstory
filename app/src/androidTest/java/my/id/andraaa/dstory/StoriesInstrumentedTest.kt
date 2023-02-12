package my.id.andraaa.dstory

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import my.id.andraaa.dstory.stories.data.service.DicodingStoryService
import my.id.andraaa.dstory.stories.data.service.response.*
import my.id.andraaa.dstory.stories.di.ApplicationModule
import my.id.andraaa.dstory.stories.domain.AuthDataSource
import my.id.andraaa.dstory.stories.domain.Session
import my.id.andraaa.dstory.stories.presentor.main.stories.StoriesFragment
import my.id.andraaa.dstory.stories.presentor.main.stories.StoryViewHolder
import okhttp3.MultipartBody
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.Thread.sleep

class StoriesKoinTestRule(
    private val modules: List<Module>
) : TestWatcher() {
    override fun starting(description: Description) {
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            modules(modules)
        }
    }

    override fun finished(description: Description) {
        stopKoin()
    }
}

@RunWith(AndroidJUnit4::class)
class StoriesInstrumentedTest {
    private val InstrumentedTestModule = module {
        single<DicodingStoryService> {
            object : DicodingStoryService {
                override suspend fun login(data: DicodingStoryService.LoginData): LoginResponse {
                    TODO("Not yet implemented")
                }

                override suspend fun register(data: DicodingStoryService.RegisterData): BaseResponse {
                    TODO("Not yet implemented")
                }

                override suspend fun addStory(
                    file: MultipartBody.Part,
                    description: String,
                    lat: Float,
                    lon: Float
                ): BaseResponse {
                    TODO("Not yet implemented")
                }

                override suspend fun getStories(
                    page: Int,
                    size: Int?,
                    filterWithLocation: Int
                ): StoriesResponse {
                    val storyFactory = StoryFactory()
                    return if (page == 3) {
                        StoriesResponse(error = false, listStory = listOf(), message = "")
                    } else {
                        StoriesResponse(
                            error = false,
                            listStory = List(10) { storyFactory.createStory() },
                            message = ""
                        )
                    }
                }

                override suspend fun getStory(id: String): StoryResponse {
                    TODO("Not yet implemented")
                }

            }
        }

        single {
            object : AuthDataSource {
                override suspend fun getSession(): Session? {
                    return Session("", "")
                }

                override suspend fun signIn(email: String, password: String) {
                    TODO("Not yet implemented")
                }

                override suspend fun signUp(name: String, email: String, password: String) {
                    TODO("Not yet implemented")
                }

                override fun signOut() {
                    TODO("Not yet implemented")
                }

            }
        }
    }

    @get:Rule
    val koinTestRule = StoriesKoinTestRule(
        modules = listOf(ApplicationModule, InstrumentedTestModule)
    )

    @Test
    fun testPager() {
        val pager = IdlingResourceSemaphore("pager")

        val storiesFragment =
            launchFragmentInContainer<StoriesFragment>(themeResId = R.style.Theme_DStory)
        storiesFragment.moveToState(Lifecycle.State.RESUMED)

        pager.increment(2)
        sleep(1000)

        onView(ViewMatchers.withId(R.id.recyclerViewStories))
            .perform(RecyclerViewActions.scrollToLastPosition<StoryViewHolder>())

        pager.increment()
        sleep(1000)

        storiesFragment.withFragment {
            val recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerViewStories)
            assertEquals(recyclerView.adapter!!.itemCount, 20)
        }
    }
}