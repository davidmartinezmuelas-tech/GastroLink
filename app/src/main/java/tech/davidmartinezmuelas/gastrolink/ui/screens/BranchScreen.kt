package tech.davidmartinezmuelas.gastrolink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.davidmartinezmuelas.gastrolink.model.Branch

@Composable
fun BranchScreen(
    branches: List<Branch>,
    onSelectBranch: (Branch) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Sucursales") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(branches, key = { it.id }) { branch ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectBranch(branch) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = branch.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = branch.city, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
