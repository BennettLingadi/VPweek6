@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.vpweek6

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF1E40AF)
private val Red = Color(0xFFDC2626)

@Composable
fun DashboardScreen(vm: CampusViewModel, modifier: Modifier = Modifier) {
    val s by vm.state.collectAsState()
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Blue), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(56.dp).background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("AP", color = Blue, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("MAHASISWA AKTIF", color = Color.White.copy(.8f), fontSize = 10.sp)
                        Text("Aditya Pratama", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Teknik Informatika • Angkatan 2022", color = Color.White, fontSize = 12.sp)
                        Text("NIM: 22001045", color = Color.White.copy(.8f), fontSize = 11.sp)
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CounterBadge("Joined Clubs", s.joined.size, Modifier.weight(1f))
                CounterBadge("Total Projects", s.projects.size, Modifier.weight(1f))
            }
        }
        item { Text("Semua Komunitas yang Diikuti (${s.joined.size})", fontWeight = FontWeight.Bold) }
        if (s.joined.isEmpty()) item { Text("Belum bergabung dengan komunitas", color = Color.Gray) }
        items(s.joined, key = { "c-${it.id}" }) { c ->
            val n = s.projects.count { p -> p.communityId == c.id }
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(c.name, fontWeight = FontWeight.SemiBold)
                        Text("${c.subtitle} • $n Proyek", fontSize = 12.sp, color = Color.Gray)
                    }
                    JoinedPill()
                }
            }
        }
        item { Text("Semua Proyek Mahasiswa (${s.projects.size})", fontWeight = FontWeight.Bold) }
        if (s.projects.isEmpty()) item { Text("Belum ada proyek yang dicatat", color = Color.Gray) }
        items(s.projects, key = { "p-${it.id}" }) { p ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(s.community(p.communityId)?.name.orEmpty(), color = Blue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(p.title, fontWeight = FontWeight.SemiBold)
                    Text(p.techStack.joinToString(" • "), fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    StatusBadge(p.status)
                }
            }
        }
    }
}

@Composable
private fun CounterBadge(label: String, value: Int, modifier: Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text("$value", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun JoinedPill() {
    Surface(shape = RoundedCornerShape(50), color = Red.copy(alpha = .1f), border = BorderStroke(1.dp, Red)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✓ Joined", color = Red, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StatusBadge(status: ProjectStatus) {
    val done = status == ProjectStatus.COMPLETED
    val c = if (done) Color(0xFF16A34A) else Color(0xFFD97706)
    Surface(shape = RoundedCornerShape(50), color = c.copy(alpha = .15f)) {
        Text(
            if (done) "Completed" else "In Progress", color = c, fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun CommunitiesScreen(
    vm: CampusViewModel, modifier: Modifier = Modifier,
    onOpen: (String) -> Unit, onBlocked: () -> Unit
) {
    val s by vm.state.collectAsState()
    Column(modifier.fillMaxSize()) {
        Text("Communities Directory", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp))
        Text("Pilih komunitas untuk membuka Club Projects", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(s.communities, key = { it.id }) { c ->
                Card(Modifier.fillMaxWidth().clickable { if (c.joined) onOpen(c.id) else onBlocked() }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(c.name, fontWeight = FontWeight.Bold)
                        Text("${c.subtitle} • ${c.members} Mahasiswa", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text(c.description, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (c.joined) "Unlocked • Buka Proyek →" else "Terkunci (Gate Akses)",
                                fontSize = 12.sp, color = if (c.joined) Blue else Color.Gray,
                                modifier = Modifier.weight(1f).clickable { if (c.joined) onOpen(c.id) else onBlocked() }
                            )
                            if (c.joined) {
                                OutlinedButton(
                                    onClick = { vm.toggleJoin(c.id) },
                                    border = BorderStroke(1.dp, Red),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Red)
                                ) { Text("✓ Joined") }
                            } else {
                                Button(onClick = { vm.toggleJoin(c.id) }) { Text("+ Join") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubProjectsScreen(
    vm: CampusViewModel, communityId: String,
    onBack: () -> Unit, onOpenProject: (String) -> Unit
) {
    val s by vm.state.collectAsState()
    val community = s.community(communityId)
    val projects = s.projects.filter { it.communityId == communityId }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${community?.name.orEmpty()} Projects") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←", fontSize = 22.sp) }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showDialog = true }) { Text("+ Add Project") }
        }
    ) { inner ->
        LazyColumn(
            Modifier.padding(inner).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Daftar Proyek Mahasiswa (${projects.size})", fontWeight = FontWeight.Bold) }
            if (projects.isEmpty()) item { Text("Belum ada proyek yang dicatat", color = Color.Gray) }
            items(projects, key = { it.id }) { p ->
                Card(Modifier.fillMaxWidth().clickable { onOpenProject(p.id) }) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text(p.title, fontWeight = FontWeight.SemiBold)
                                Text(p.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                            }
                            IconButton(
                                onClick = { vm.deleteProject(p.id) },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Red.copy(.15f), contentColor = Red),
                                modifier = Modifier.size(32.dp)
                            ) { Text("−", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                        }
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            p.techStack.forEach { AssistChip(onClick = {}, label = { Text(it, fontSize = 10.sp) }) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ID: ${p.id}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                            StatusBadge(p.status)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddProjectDialog(
            communityName = "${community?.name.orEmpty()} (${community?.subtitle.orEmpty()})",
            onCancel = { showDialog = false },
            onSave = { t, d, st -> vm.addProject(communityId, t, d, st); showDialog = false }
        )
    }
}

@Composable
private fun AddProjectDialog(
    communityName: String, onCancel: () -> Unit,
    onSave: (String, String, List<String>) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }
    val valid = title.isNotBlank() && desc.isNotBlank() && selected.isNotEmpty()

    BasicAlertDialog(onDismissRequest = { }) {
        Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 6.dp) {
            Column(Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text("Add Project", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Komunitas: $communityName", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    title, { title = it }, label = { Text("Judul Proyek *") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    desc, { desc = it }, label = { Text("Deskripsi Proyek (Minimal 3 Baris) *") },
                    minLines = 3, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    Text("Tech Stack (Pilihan Multi-Selection) *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("${selected.size} Dipilih", fontSize = 11.sp, color = Blue)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TECH_OPTIONS.forEach { tech ->
                        val on = tech in selected
                        FilterChip(
                            selected = on,
                            onClick = { selected = if (on) selected - tech else selected + tech },
                            label = { Text(if (on) "✓ $tech" else "+ $tech") }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(title, desc, TECH_OPTIONS.filter { it in selected }) },
                        enabled = valid
                    ) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun ProjectDetailScreen(vm: CampusViewModel, projectId: String, onBack: () -> Unit) {
    val s by vm.state.collectAsState()
    val project = s.project(projectId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Detail") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←", fontSize = 22.sp) }
                }
            )
        }
    ) { inner ->
        if (project == null) {
            Box(Modifier.padding(inner).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Proyek tidak ditemukan")
            }
            return@Scaffold
        }
        Column(
            Modifier.padding(inner).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = Blue) {
                        Text(
                            s.community(project.communityId)?.name.orEmpty(), color = Color.White, fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(project.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Mark as Done", fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Status: ", fontSize = 12.sp, color = Color.Gray)
                            StatusBadge(project.status)
                        }
                    }
                    Switch(
                        checked = project.status == ProjectStatus.COMPLETED,
                        onCheckedChange = { vm.toggleStatus(project.id) }
                    )
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Deskripsi Lengkap Proyek", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(project.description, fontSize = 13.sp)
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tech Stack Terpilih", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        project.techStack.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                    }
                }
            }
        }
    }
}
 