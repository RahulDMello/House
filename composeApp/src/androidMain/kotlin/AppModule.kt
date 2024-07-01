import network.HouseApi
import org.example.house.HouseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<HouseApi> { HouseApi() }
    single<HouseSDK> {
        HouseSDK(
            api = get()
        )
    }
    viewModel { HouseViewModel(sdk = get()) }
}
