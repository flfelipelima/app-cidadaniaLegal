package com.example.cidadanialegal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Estrutura de Dados ---
data class DireitoTopico(val titulo: String, val descricao: String)
data class DireitoCategoria(val titulo: String, val icon: ImageVector, val topicos: List<DireitoTopico>)
data class GlossarioTermo(val termo: String, val definicao: String)
data class FaqItem(val pergunta: String, val resposta: String)
data class Mensagem(val texto: String, val eDoUsuario: Boolean, val estaEscrevendo: Boolean = false)

// --- Rotas de Navegação ---
object Routes {
    const val HOME = "home"
    const val MEUS_DIREITOS = "meus_direitos"
    const val TIRA_DUVIDAS = "tira_duvidas"
    const val GERADOR_DOCS = "gerador_docs"
    const val FAQ = "faq"
    const val DENUNCIA = "denuncia"
    const val PARCEIROS = "parceiros"
    const val GLOSSARIO = "glossario"
}

// --- Ponto de Entrada da Aplicação ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CidadaniaLegalApp()
        }
    }
}

// --- Tema da Aplicação ---
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Color(0xFF005A9C),
        secondary = Color(0xFFE87722),
        background = Color(0xFFF7F9FC), // Um cinza azulado muito claro
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF1A1C1E),
        onSurface = Color(0xFF1A1C1E),
        error = Color(0xFFB00020),
        surfaceVariant = Color(0xFFEDF2F9) // Cor de fundo para cards e elementos de UI
    )

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = colors.primary),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp)
        ),
        shapes = Shapes(
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}

// --- Componente Principal com Navegação ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CidadaniaLegalApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("⚖️ Cidadania Legal") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        val canPop = navController.previousBackStackEntry != null
                        AnimatedVisibility(visible = canPop, enter = fadeIn(), exit = fadeOut()) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(paddingValues),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                composable(Routes.HOME) { HomeScreen(navController) }
                composable(Routes.MEUS_DIREITOS) { MeusDireitosScreen(conteudoDireitos) }
                composable(Routes.TIRA_DUVIDAS) { TiraDuvidasScreen() }
                composable(Routes.GERADOR_DOCS) { GeradorDocumentosScreen(snackbarHostState) }
                composable(Routes.FAQ) { FaqScreen(conteudoFaq) }
                composable(Routes.DENUNCIA) { DenunciaScreen() }
                composable(Routes.PARCEIROS) { ParceirosScreen() }
                composable(Routes.GLOSSARIO) { GlossarioScreen(conteudoGlossario) }
            }
        }
    }
}

// --- ECRÃS ---

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Olá!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        "Bem-vindo(a) ao Cidadania Legal. Como podemos ajudar hoje?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            PrimaryFeatureButton(
                navController = navController,
                route = Routes.MEUS_DIREITOS,
                title = "Conheça Seus Direitos",
                subtitle = "O primeiro passo é a informação. Navegue por temas e entenda seus direitos.",
                icon = Icons.Filled.Gavel
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            Text("Ferramentas Interativas", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SecondaryFeatureButton(navController, Routes.TIRA_DUVIDAS, "Tira-Dúvidas", Icons.Filled.QuestionAnswer, Modifier.weight(1f))
                SecondaryFeatureButton(navController, Routes.GERADOR_DOCS, "Documentos", Icons.Filled.Description, Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            Text("Apoio e Recursos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            SupportFeatureButton(navController, Routes.GLOSSARIO, "Juridiquês", "Traduza termos legais complicados.", Icons.Filled.Translate)
            SupportFeatureButton(navController, Routes.FAQ, "Dúvidas Frequentes", "Encontre respostas para perguntas comuns.", Icons.Filled.Quiz)
            SupportFeatureButton(navController, Routes.PARCEIROS, "Encontre Apoio", "Conecte-se com ONGs e Defensorias.", Icons.Filled.People)
            SupportFeatureButton(navController, Routes.DENUNCIA, "Denúncia Anônima", "Registre violações de direitos de forma segura.", Icons.Filled.Report, isDestructive = true)
        }
    }
}

@Composable
fun MeusDireitosScreen(categorias: List<DireitoCategoria>) {
    var topicoSelecionado by remember { mutableStateOf<DireitoTopico?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Meus Direitos", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            Text("Selecione uma categoria para ver os tópicos e toque num tópico para saber mais detalhes.", style = MaterialTheme.typography.bodyMedium)
        }
        items(categorias) { categoria ->
            CategoriaDireitoItem(categoria = categoria, onTopicClick = { topico ->
                topicoSelecionado = topico
            })
        }
    }

    if (topicoSelecionado != null) {
        AlertDialog(
            onDismissRequest = { topicoSelecionado = null },
            title = { Text(topicoSelecionado!!.titulo, style = MaterialTheme.typography.titleLarge) },
            text = { Text(topicoSelecionado!!.descricao, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = { topicoSelecionado = null }) {
                    Text("FECHAR")
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}

@Composable
fun TiraDuvidasScreen() {
    val mensagens = remember { mutableStateListOf(Mensagem("Olá! Faça uma pergunta sobre seus direitos. Ex: 'Quais os meus direitos se fui demitido?'", false)) }
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var estaAProcessar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = remember { androidx.compose.foundation.lazy.LazyListState() }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mensagens) { msg -> MessageBubble(msg) }
        }

        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Digite sua pergunta...") },
                enabled = !estaAProcessar,
                shape = MaterialTheme.shapes.medium
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (input.text.isNotBlank()) {
                        mensagens.add(Mensagem(input.text, true))
                        input = TextFieldValue("")
                        estaAProcessar = true
                        mensagens.add(Mensagem("", false, estaEscrevendo = true))
                        scope.launch { listState.animateScrollToItem(mensagens.size - 1) }

                        scope.launch {
                            delay(2500)
                            mensagens.removeAt(mensagens.size - 1) // Remove o indicador "a escrever"
                            mensagens.add(Mensagem("Importante: esta é uma orientação geral baseada em IA e não substitui a consulta com um advogado. Para o seu caso específico, a recomendação é sempre procurar a Defensoria Pública.", false))
                            estaAProcessar = false
                            listState.animateScrollToItem(mensagens.size - 1)
                        }
                    }
                },
                enabled = !estaAProcessar && input.text.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Enviar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeradorDocumentosScreen(snackbarHostState: SnackbarHostState) {
    var descricao by remember { mutableStateOf("") }
    var tipoDocumento by remember { mutableStateOf("E-mail Formal") }
    var documentoGerado by remember { mutableStateOf<String?>(null) }
    var estaAGerar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Gerador de Documentos", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descreva seu problema") }, modifier = Modifier.fillMaxWidth().height(150.dp))

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = tipoDocumento, onValueChange = {}, readOnly = true, label = { Text("Tipo de Documento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("E-mail Formal", "Carta de Reclamação", "Modelo de Reclamação").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { tipoDocumento = it; expanded = false })
                }
            }
        }

        Button(
            onClick = {
                if(descricao.isNotBlank()) {
                    estaAGerar = true
                    scope.launch {
                        delay(1500)
                        documentoGerado = """Prezados(as),\n\nEu, [Nome Completo], CPF [Seu CPF], venho por meio deste documento registrar a seguinte questão:\n\n$descricao\n\nDiante do exposto, solicito uma solução.\nAguardando retorno.\n\nAtenciosamente,\n[Seu Nome Completo]\n[Sua Cidade], [Data]""".trimIndent()
                        estaAGerar = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !estaAGerar
        ) {
            if (estaAGerar) { CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp) } else { Text("Gerar Rascunho") }
        }

        AnimatedVisibility(visible = documentoGerado != null) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rascunho Gerado:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(documentoGerado!!))
                            scope.launch { snackbarHostState.showSnackbar("Texto copiado!") }
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copiar Texto")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(documentoGerado ?: "", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun FaqScreen(faqs: List<FaqItem>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Dúvidas Frequentes", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp)) }
        items(faqs) { (pergunta, resposta) -> FaqItemCard(pergunta, resposta) }
    }
}

@Composable
fun GlossarioScreen(termos: List<GlossarioTermo>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Glossário Jurídico (Juridiquês)", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp)) }
        items(termos) { (termo, definicao) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(termo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text(definicao, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenunciaScreen() {
    var descricao by remember { mutableStateOf("") }
    var tipoViolacao by remember { mutableStateOf("Discriminação") }
    var denunciaEnviada by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if(denunciaEnviada) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha=0.1f)), shape = MaterialTheme.shapes.large) {
                Column(Modifier.padding(24.dp)) {
                    Text("Denúncia registrada com sucesso.", style = MaterialTheme.typography.titleLarge)
                    Text("O seu registro anônimo ajuda a mapear violações de direitos. Para uma ação imediata, use os canais oficiais como o Disque 100.", modifier = Modifier.padding(top=8.dp))
                    Button(onClick = { denunciaEnviada = false }, modifier=Modifier.padding(top=16.dp)) {
                        Text("Fazer outra denúncia")
                    }
                }
            }
        } else {
            Text("Registrar Violação de Direitos", style = MaterialTheme.typography.headlineSmall)
            Text("Este canal é para registro anônimo e não solicita dados pessoais. Sua denúncia é confidencial.", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descreva a violação") }, modifier = Modifier.fillMaxWidth().height(150.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = tipoViolacao, onValueChange = {}, readOnly = true, label = { Text("Tipo de Violação") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Discriminação", "Violência Física/Psicológica", "Abuso de Autoridade", "Outro").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { tipoViolacao = it; expanded = false })
                    }
                }
            }

            Button(
                onClick = {
                    denunciaEnviada = true
                    descricao = ""
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Enviar Denúncia Anônima")
            }
        }
    }
}

@Composable
fun ParceirosScreen() {
    val parceiros = listOf(
        "Defensoria Pública do Estado" to "Assistência jurídica gratuita. Verifique o endereço e telefone da sua cidade.",
        "Centro de Referência da Mulher" to "Apoio psicossocial e jurídico para mulheres em situação de violência.",
        "ONG Cidadania LGBTQIA+" to "Acolhimento e orientação para a população LGBTQIA+.",
        "Movimento Negro Unificado" to "Atua na luta contra o racismo e pela igualdade racial."
    )

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Parceiros e Apoio", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp)) }
        items(parceiros) { (nome, descricao) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(nome, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(descricao, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}


// --- COMPONENTES DE UI REUTILIZÁVEIS ---

@Composable
fun PrimaryFeatureButton(navController: NavController, route: String, title: String, subtitle: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(route) },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = LocalContentColor.current.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun SecondaryFeatureButton(navController: NavController, route: String, title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { navController.navigate(route) },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SupportFeatureButton(navController: NavController, route: String, title: String, subtitle: String, icon: ImageVector, isDestructive: Boolean = false) {
    val color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { navController.navigate(route) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = LocalContentColor.current.copy(alpha = 0.7f))
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}


@Composable
fun CategoriaDireitoItem(categoria: DireitoCategoria, onTopicClick: (DireitoTopico) -> Unit) {
    var expandido by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Row(
                modifier = Modifier.clickable { expandido = !expandido }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(categoria.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Text(categoria.titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expandido) "Recolher" else "Expandir"
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            ) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    categoria.topicos.forEach { topico ->
                        Row(
                            Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable { onTopicClick(topico) }.padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(topico.titulo, modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Ver detalhes", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(mensagem: Mensagem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensagem.eDoUsuario) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = if (mensagem.eDoUsuario) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (mensagem.estaEscrevendo) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 18.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            } else {
                Text(text = mensagem.texto, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun FaqItemCard(pergunta: String, resposta: String) {
    var expandido by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().clickable { expandido = !expandido }) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pergunta, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(visible = expandido) {
                Text(resposta, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// --- DADOS DA APLICAÇÃO (CONTEÚDO) ---

val conteudoDireitos = listOf(
    DireitoCategoria(
        titulo = "Direito do Consumidor",
        icon = Icons.Filled.ShoppingCart,
        topicos = listOf(
            DireitoTopico("Produto com Defeito", "• Produtos não duráveis (alimentos): 30 dias para reclamar.\n• Produtos duráveis (eletrónicos): 90 dias para reclamar.\nA loja tem 30 dias para consertar. Se não o fizer, você pode pedir um produto novo, seu dinheiro de volta ou um desconto."),
            DireitoTopico("Cobrança Indevida", "Se você pagar uma conta que não devia ou com valor errado, tem direito a receber de volta o dobro do que pagou a mais."),
            DireitoTopico("Direito de Arrependimento", "Para compras feitas fora da loja física (internet, telefone), você tem 7 dias, a contar da data de recebimento, para se arrepender, devolver o produto e receber o dinheiro de volta, sem precisar de um motivo.")
        )
    ),
    DireitoCategoria(
        titulo = "Direito Trabalhista",
        icon = Icons.Filled.Work,
        topicos = listOf(
            DireitoTopico("Demissão Sem Justa Causa", "Você tem direito a:\n• Saldo de salário (dias trabalhados no mês).\n• Aviso prévio (trabalhado ou indenizado).\n• Férias vencidas e proporcionais + 1/3.\n• 13º salário proporcional.\n• Sacar o FGTS + multa de 40% paga pela empresa.\n• Seguro-desemprego (se cumprir os requisitos)."),
            DireitoTopico("Acidente de Trabalho", "A empresa deve emitir a CAT (Comunicação de Acidente de Trabalho). Você tem direito a estabilidade no emprego por 12 meses após retornar do auxílio-doença do INSS."),
            DireitoTopico("Horas Extras", "As horas que você trabalha além da sua jornada normal devem ser pagas com um acréscimo de, no mínimo, 50% sobre o valor da hora normal.")
        )
    ),
    DireitoCategoria(
        titulo = "Violência Doméstica",
        icon = Icons.Filled.Favorite,
        topicos = listOf(
            DireitoTopico("Tipos de Violência", "A Lei Maria da Penha protege contra 5 tipos de violência:\n• Física (agressões)\n• Psicológica (ameaças, humilhação)\n• Sexual (forçar atos sexuais)\n• Patrimonial (reter dinheiro, destruir bens)\n• Moral (calúnia, difamação)."),
            DireitoTopico("Medidas Protetivas", "São ordens judiciais para proteger a vítima. O agressor pode ser proibido de se aproximar ou de entrar em contato. Podem ser pedidas em qualquer delegacia, de preferência na Delegacia da Mulher.")
        )
    )
)

val conteudoGlossario = listOf(
    GlossarioTermo("Habeas Corpus", "É uma ação judicial para proteger o direito de liberdade de uma pessoa quando ela é presa ou ameaçada de ser presa ilegalmente."),
    GlossarioTermo("Jurisprudência", "É o conjunto de decisões e interpretações que os tribunais dão para as leis. Serve como um guia para casos futuros parecidos."),
    GlossarioTermo("Petição Inicial", "É o primeiro documento que se apresenta à Justiça para iniciar um processo, explicando o caso e o que se está a pedir."),
    GlossarioTermo("Trânsito em Julgado", "Uma decisão judicial da qual não se pode mais recorrer, ou seja, é definitiva."),
    GlossarioTermo("Liminar", "Uma decisão rápida e provisória que o juiz toma no início de um processo para evitar um dano grave e urgente, antes da decisão final.")
)

val conteudoFaq = listOf(
    FaqItem("O que é a Defensoria Pública?", "É uma instituição que presta assistência jurídica gratuita para quem não pode pagar um advogado."),
    FaqItem("Como peço uma medida protetiva?", "Você pode ir a uma Delegacia da Mulher ou a qualquer delegacia de polícia e registrar um boletim de ocorrência, solicitando as medidas protetivas."),
    FaqItem("Meu nome foi para o SPC/Serasa indevidamente. O que faço?", "Primeiro, contate a empresa para solicitar a retirada. Se não resolver, você pode procurar o Procon ou a Defensoria Pública."),
    FaqItem("Sofri um acidente de trabalho, quais meus direitos?", "Você tem direito à estabilidade no emprego por 12 meses após o retorno, além do auxílio-doença acidentário pago pelo INSS.")
)


// --- PREVIEWS ---
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun MeusDireitosScreenPreview() {
    AppTheme {
        MeusDireitosScreen(conteudoDireitos)
    }
}

