import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.components.customColor
import com.pyco.app.models.Request
import com.pyco.app.navigation.Routes
import com.pyco.app.viewmodels.HomeViewModel

@Composable
fun RequestsFeed(
    homeViewModel: HomeViewModel,
    navController: NavHostController
) {
    val requests by homeViewModel.globalRequests.collectAsState()
    val isLoading by homeViewModel.isLoadingRequests.collectAsState()
    val errorMessage by homeViewModel.requestError.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchGlobalRequests()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (!errorMessage.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage ?: "Unknown error occurred.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else if (requests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No requests to display",
                style = MaterialTheme.typography.bodyLarge,
                color = customColor
            )
        }
    } else {
        val pagerState = rememberPagerState { requests.size }

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val request = requests[page]

            RequestCard(
                request = request,
                navController = navController
            )
        }
    }
}

@Composable
fun RequestCard(
    request: Request,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = request.description,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = customColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Created by: ${request.ownerName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Created at: ${request.timestamp ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Button(
                onClick = {
                    navController.navigate("${Routes.CREATE_RESPONSE}?requestId=${request.id}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Respond to this Request")
            }
        }
    }
}
