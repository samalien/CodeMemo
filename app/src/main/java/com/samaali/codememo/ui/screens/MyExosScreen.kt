package com.samaali.codememo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.samaali.codememo.data.model.UserExercise
import com.samaali.codememo.data.repository.UserExerciseRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyExosScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { UserExerciseRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val exos by repository.getAll().collectAsState(initial = emptyList())

    // États pour le dialog d'ajout
    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pseudocode by remember { mutableStateOf("") }
    var python by remember { mutableStateOf("") }
    var exampleInput by remember { mutableStateOf("") }
    var exampleOutput by remember { mutableStateOf("") }

    // États pour la confirmation de suppression
    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<UserExercise?>(null) }

    // Dialog d'ajout d'un exo
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Ajouter un exo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") }, singleLine = true)
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, singleLine = true)
                    OutlinedTextField(value = pseudocode, onValueChange = { pseudocode = it }, label = { Text("Pseudo-code") }, minLines = 3)
                    OutlinedTextField(value = python, onValueChange = { python = it }, label = { Text("Code Python") }, minLines = 5)
                    OutlinedTextField(value = exampleInput, onValueChange = { exampleInput = it }, label = { Text("Exemple entrée") })
                    OutlinedTextField(value = exampleOutput, onValueChange = { exampleOutput = it }, label = { Text("Exemple sortie") })
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            repository.insert(
                                UserExercise(
                                    name = name,
                                    description = description,
                                    pseudocode = pseudocode,
                                    python = python,
                                    exampleInput = exampleInput,
                                    exampleOutput = exampleOutput
                                )
                            )
                        }
                        showAddDialog = false
                    }
                ) {
                    Text("Sauvegarder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Dialog de confirmation de suppression
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer l'exercice ?") },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir supprimer \"${exerciseToDelete?.name}\" ?\nCette action est irréversible.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            exerciseToDelete?.id?.let { repository.deleteById(it) }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mes Exos", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un exo")
            }
        }
    ) { padding ->
        if (exos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = "Aucun exercice personnalisé.\nAppuyez sur + pour en créer un !",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(exos.size) { index ->
                    val exo = exos[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        // CLIC SUR LA CARTE → navigation vers le détail (réutilise la route Detail)
                        onClick = {
                            navController.navigate("detail/${exo.id}")
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exo.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                if (exo.description.isNotBlank()) {
                                    Text(
                                        text = exo.description,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    exerciseToDelete = exo
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer l'exercice",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}