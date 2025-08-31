package com.example.cidadanialegal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.cidadanialegal.R
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip

// --- Estrutura de Dados ---
data class DireitoTopico(val titulo: String, val descricao: String)
data class DireitoCategoria(val titulo: String, val icon: ImageVector, val topicos: List<DireitoTopico>)
data class GlossarioTermo(val termo: String, val definicao: String)
data class GlossarioCategoria(val nome: String, val icon: ImageVector, val termos: List<GlossarioTermo>)
data class FaqItem(val pergunta: String, val resposta: String)
data class Mensagem(val texto: String, val eDoUsuario: Boolean, val estaEscrevendo: Boolean = false)
data class ParceiroPrincipal(val icon: ImageVector, val nome: String, val descricao: String, val telefone: String?, val site: String?)


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
        primary = Color(0xFF315FB4),
        secondary = Color(0xFFE87722),
        background = Color(0xFFF7F9FC),
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF1A1C1E),
        onSurface = Color(0xFF1A1C1E),
        error = Color(0xFFB00020),
        surfaceVariant = Color(0xFFEDF2F9)
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
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppLogo(modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Cidadania Legal")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        val canPop = navController.previousBackStackEntry != null
                        AnimatedVisibility(visible = canPop, enter = fadeIn(), exit = fadeOut()) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
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
                composable(Routes.GLOSSARIO) { GlossarioScreen(categoriasGlossario) }
            }
        }
    }
}

// --- ECRÃS ---

// --- GLOSSÁRIO (NOVA VERSÃO COM POP-UP) ---
@Composable
fun GlossarioScreen(categorias: List<GlossarioCategoria>) {
    var searchQuery by remember { mutableStateOf("") }
    var termoSelecionado by remember { mutableStateOf<GlossarioTermo?>(null) }

    val categoriasFiltradas = remember(searchQuery, categorias) {
        if (searchQuery.isBlank()) {
            categorias
        } else {
            categorias.mapNotNull { categoria ->
                val termosFiltrados = categoria.termos.filter { termo ->
                    termo.termo.contains(searchQuery, ignoreCase = true) ||
                            termo.definicao.contains(searchQuery, ignoreCase = true)
                }
                if (termosFiltrados.isNotEmpty()) {
                    categoria.copy(termos = termosFiltrados)
                } else {
                    null
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Decifrando o Juridiquês", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Encontre aqui a tradução de termos complicados do mundo jurídico para uma linguagem que todos entendem.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Pesquisar termo...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Pesquisar") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
        }

        if (categoriasFiltradas.isEmpty()) {
            item {
                Text(
                    "Nenhum termo encontrado para \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            }
        } else {
            items(categoriasFiltradas) { categoria ->
                CategoriaGlossarioItem(
                    categoria = categoria,
                    onTermoClick = { termo ->
                        termoSelecionado = termo
                    }
                )
            }
        }
    }

    if (termoSelecionado != null) {
        AlertDialog(
            onDismissRequest = { termoSelecionado = null },
            title = { Text(termoSelecionado!!.termo, style = MaterialTheme.typography.titleLarge) },
            text = { Text(termoSelecionado!!.definicao, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = { termoSelecionado = null }) {
                    Text("FECHAR")
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}

@Composable
fun GeradorDocumentosScreen(snackbarHostState: SnackbarHostState) {
    var nome by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var assunto by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var documentoGerado by remember { mutableStateOf<String?>(null) }
    var estaAGerar by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val formCompleto = nome.isNotBlank() && cidade.isNotBlank() && assunto.isNotBlank() && descricao.isNotBlank()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Gerador de Documentos", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Preencha os campos abaixo para criar um rascunho de documento formal.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }

        item { FormTextField(value = nome, onValueChange = { nome = it }, label = "Seu Nome Completo", icon = Icons.Filled.Person) }
        item { FormTextField(value = cidade, onValueChange = { cidade = it }, label = "Sua Cidade", icon = Icons.Filled.LocationCity) }
        item { FormTextField(value = assunto, onValueChange = { assunto = it }, label = "Assunto", placeholder = "Ex: Cobrança indevida, produto com defeito", icon = Icons.Filled.Edit) }
        item {
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descreva o problema detalhadamente") },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = MaterialTheme.shapes.medium
            )
        }

        item {
            Button(
                onClick = {
                    estaAGerar = true
                    scope.launch {
                        delay(1500) // Simulação de processamento
                        val dataAtual = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(Date())
                        documentoGerado = """
                            **Para:** [Nome da Empresa ou Destinatário]
                            **De:** $nome
                            **Assunto:** $assunto

                            Prezados(as),

                            Eu, $nome, residente em $cidade, venho por meio desta comunicação formalizar a seguinte questão:

                            $descricao

                            Diante do exposto, solicito uma análise e uma solução para o problema apresentado.

                            Agradeço a atenção e aguardo um breve retorno.

                            Atenciosamente,

                            $nome
                            $cidade, $dataAtual
                        """.trimIndent()
                        estaAGerar = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = formCompleto && !estaAGerar
            ) {
                if (estaAGerar) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Gerar Rascunho do Documento")
                }
            }
        }

        item {
            AnimatedVisibility(visible = documentoGerado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
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
                        Text(documentoGerado ?: "", style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                    }
                }
            }
        }
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
            items(mensagens) { msg ->
                if(msg.estaEscrevendo) MessageBubbleLoading() else MessageBubble(msg)
            }
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
                            mensagens.removeIf { it.estaEscrevendo }
                            mensagens.add(Mensagem("Importante: esta é uma orientação geral e não substitui a consulta com um advogado. Para o seu caso específico, a recomendação é sempre procurar a Defensoria Pública.", false))
                            estaAProcessar = false
                            listState.animateScrollToItem(mensagens.size - 1)
                        }
                    }
                },
                enabled = !estaAProcessar && input.text.isNotBlank(),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
            }
        }
    }
}

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
                    Text( "Olá!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Text( "Bem-vindo(a) ao Cidadania Legal. Como podemos ajudar hoje?", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(top = 8.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            PrimaryFeatureButton(navController = navController, route = Routes.MEUS_DIREITOS, title = "Conheça Seus Direitos", subtitle = "O primeiro passo é a informação. Navegue por temas e entenda seus direitos.", icon = Icons.Filled.Gavel)
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
            SupportFeatureButton(navController, Routes.GLOSSARIO, "Decifrando o Juridiquês", "Traduza termos legais complicados.", Icons.Filled.Translate)
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
fun FaqScreen(faqs: List<FaqItem>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Dúvidas Frequentes", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp)) }
        items(faqs) { (pergunta, resposta) -> FaqItemCard(pergunta, resposta) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenunciaScreen() {
    var descricao by remember { mutableStateOf("") }
    var tipoViolacao by remember { mutableStateOf("Discriminação") }
    var denunciaEnviada by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
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
                OutlinedTextField(value = tipoViolacao, onValueChange = {}, readOnly = true, label = { Text("Tipo de Violação") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Discriminação", "Violência Física/Psicológica", "Abuso de Autoridade", "Outro").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { tipoViolacao = it; expanded = false })
                    }
                }
            }
            Button(onClick = { denunciaEnviada = true; descricao = "" }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Enviar Denúncia Anônima")
            }
        }
    }
}

@Composable
fun ParceirosScreen() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Icon(imageVector = Icons.Filled.Handshake, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center).size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Parceiros e Apoio", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text("Organizações parceiras que podem ajudar você", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }
        item { EmergencyNumbersCard() }
        items(parceirosPrincipais) { parceiro -> ParceiroPrincipalCard(parceiro = parceiro) }
        item { OutrasOrganizacoesCard() }
    }
}

// --- COMPONENTES DE UI REUTILIZÁVEIS ---
@Composable
fun CategoriaGlossarioItem(categoria: GlossarioCategoria, onTermoClick: (GlossarioTermo) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Row(
                modifier = Modifier.clickable { expandido = !expandido }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(categoria.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Text(categoria.nome, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Icon(if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }

            AnimatedVisibility(visible = expandido, enter = expandVertically(), exit = shrinkVertically()) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    categoria.termos.forEach { termo ->
                        Row(
                            Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable { onTermoClick(termo) }.padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(termo.termo, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Ver detalhes", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, placeholder: String? = null) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { placeholder?.let { Text(it) } },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    )
}

@Composable
fun AppLogo(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Box(
        modifier = modifier
            .background(Color.White, shape = CircleShape)
            .padding(4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_c),
            contentDescription = "Logomarca Cidadania Legal",
            modifier = Modifier.fillMaxSize()
        )
    }
}

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
                Icon(if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(visible = expandido, enter = expandVertically(), exit = shrinkVertically()) {
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
            Text(text = mensagem.texto, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MessageBubbleLoading() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("A pensar...", color = LocalContentColor.current.copy(alpha = 0.7f))
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

@Composable
fun EmergencyNumbersCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Call, contentDescription = "Números de Emergência", tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Números de Emergência", style = MaterialTheme.typography.titleMedium)
                Text("190 - Polícia | 193 - Bombeiros | 192 - SAMU", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ParceiroPrincipalCard(parceiro: ParceiroPrincipal) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = parceiro.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(parceiro.nome, style = MaterialTheme.typography.titleLarge)
                    Text(parceiro.descricao, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (parceiro.telefone != null) {
                    Button(
                        onClick = { val intent = Intent(Intent.ACTION_DIAL, "tel:${parceiro.telefone}".toUri()); context.startActivity(intent) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = "Ligar", tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(parceiro.telefone, color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (parceiro.site != null) {
                    Button(
                        onClick = { val intent = Intent(Intent.ACTION_VIEW, parceiro.site.toUri()); context.startActivity(intent) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Filled.Language, contentDescription = "Site", tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Site", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun OutrasOrganizacoesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Favorite, contentDescription = "Outras Organizações", tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text("Outras Organizações de Apoio", style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.secondary))
            }
            Spacer(Modifier.height(16.dp))
            outrosParceiros.forEach { nome ->
                Text("• $nome", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

// --- DADOS DA APLICAÇÃO (CONTEÚDO) ---

val conteudoDireitos = listOf(
    DireitoCategoria("Direito do Consumidor", Icons.Filled.ShoppingCart, listOf(DireitoTopico("Produto com Defeito", "• Produtos não duráveis (alimentos): 30 dias para reclamar.\n• Produtos duráveis (eletrónicos): 90 dias para reclamar.\nA loja tem 30 dias para consertar. Se não o fizer, você pode pedir um produto novo, seu dinheiro de volta ou um desconto."), DireitoTopico("Cobrança Indevida", "Se você pagar uma conta que não devia ou com valor errado, tem direito a receber de volta o dobro do que pagou a mais."), DireitoTopico("Direito de Arrependimento", "Para compras feitas fora da loja física (internet, telefone), você tem 7 dias, a contar da data de recebimento, para se arrepender, devolver o produto e receber o dinheiro de volta, sem precisar de um motivo."))),
    DireitoCategoria("Direito Trabalhista", Icons.Filled.Work, listOf(DireitoTopico("Demissão Sem Justa Causa", "Você tem direito a:\n• Saldo de salário (dias trabalhados no mês).\n• Aviso prévio (trabalhado ou indenizado).\n• Férias vencidas e proporcionais + 1/3.\n• 13º salário proporcional.\n• Sacar o FGTS + multa de 40% paga pela empresa.\n• Seguro-desemprego (se cumprir os requisitos)."), DireitoTopico("Acidente de Trabalho", "A empresa deve emitir a CAT (Comunicação de Acidente de Trabalho). Você tem direito a estabilidade no emprego por 12 meses após retornar do auxílio-doença do INSS."), DireitoTopico("Horas Extras", "As horas que você trabalha além da sua jornada normal devem ser pagas com um acréscimo de, no mínimo, 50% sobre o valor da hora normal."))),
    DireitoCategoria("Violência Doméstica", Icons.Filled.Favorite, listOf(DireitoTopico("Tipos de Violência", "A Lei Maria da Penha protege contra 5 tipos de violência:\n• Física (agressões)\n• Psicológica (ameaças, humilhação)\n• Sexual (forçar atos sexuais)\n• Patrimonial (reter dinheiro, destruir bens)\n• Moral (calúnia, difamação)."), DireitoTopico("Medidas Protetivas", "São ordens judiciais para proteger a vítima. O agressor pode ser proibido de se aproximar ou de entrar em contato. Podem ser pedidas em qualquer delegacia, de preferência na Delegacia da Mulher.")))
)

val categoriasGlossario = listOf(
    GlossarioCategoria("Direito de Família", Icons.Filled.FamilyRestroom, listOf(
        GlossarioTermo("Alimentos (Pensão Alimentícia)", "Valor pago para ajudar no sustento de filhos ou ex-cônjuge que não consegue se manter sozinho."),
        GlossarioTermo("Guarda Compartilhada", "Quando pai e mãe, mesmo separados, tomam as decisões importantes sobre a vida dos filhos em conjunto."),
        GlossarioTermo("Tutela", "Quando um adulto é nomeado por um juiz para cuidar de um menor de idade que não tem pais."),
        GlossarioTermo("Divórcio", "Processo legal que encerra oficialmente um casamento."),
        GlossarioTermo("União Estável", "Quando um casal vive junto como se fosse casado, de forma pública e com a intenção de constituir família.")
    )),
    GlossarioCategoria("Direito do Trabalho", Icons.Filled.Work, listOf(
        GlossarioTermo("Rescisão", "É o fim do contrato de trabalho, seja por demissão ou por pedido de demissão."),
        GlossarioTermo("Justa Causa", "Demissão por uma falta grave cometida pelo empregado, que perde a maioria dos seus direitos."),
        GlossarioTermo("FGTS", "Fundo de Garantia do Tempo de Serviço. Um valor que a empresa deposita todo mês numa conta do empregado."),
        GlossarioTermo("INSS", "Instituto Nacional do Seguro Social. Responsável pela aposentadoria, auxílio-doença e outros benefícios."),
        GlossarioTermo("Aviso Prévio", "Comunicação antecipada do fim do contrato de trabalho, que deve ser feita com pelo menos 30 dias de antecedência.")
    )),
    GlossarioCategoria("Direito do Consumidor", Icons.Filled.ShoppingCart, listOf(
        GlossarioTermo("Vício Oculto", "Defeito de fabricação que não é aparente e só se manifesta depois de um tempo de uso do produto."),
        GlossarioTermo("Prazo de Arrependimento", "Direito de desistir de uma compra feita pela internet ou telefone em até 7 dias após o recebimento."),
        GlossarioTermo("Garantia Legal", "Garantia obrigatória por lei. São 30 dias para produtos não duráveis e 90 dias para produtos duráveis."),
        GlossarioTermo("Oferta", "Toda informação ou publicidade sobre um produto ou serviço. A empresa é obrigada a cumprir o que prometeu.")
    )),
    GlossarioCategoria("Direito Criminal", Icons.Filled.LocalPolice, listOf(
        GlossarioTermo("Flagrante Delito", "Quando alguém é pego cometendo um crime ou logo após cometê-lo."),
        GlossarioTermo("Inquérito Policial", "Investigação conduzida pela polícia para apurar um crime e descobrir quem o cometeu."),
        GlossarioTermo("Denúncia", "Peça inicial do processo criminal, feita pelo Ministério Público, acusando alguém formalmente de um crime."),
        GlossarioTermo("Queixa-Crime", "Peça inicial de alguns processos criminais, feita pela própria vítima ou seu representante legal.")
    )),
    GlossarioCategoria("Termos Gerais", Icons.Filled.Gavel, listOf(
        GlossarioTermo("Habeas Corpus", "Ação para proteger o direito de liberdade de alguém que foi preso ou está ameaçado de ser preso ilegalmente."),
        GlossarioTermo("Jurisprudência", "Conjunto de decisões dos tribunais sobre um mesmo tema, que serve de orientação para casos futuros."),
        GlossarioTermo("Petição Inicial", "Documento que inicia um processo na Justiça, onde se explica o caso e o que se está a pedir."),
        GlossarioTermo("Liminar", "Decisão rápida e provisória de um juiz no início de um processo para evitar um dano urgente."),
        GlossarioTermo("Trânsito em Julgado", "Quando uma decisão judicial se torna definitiva e não se pode mais recorrer.")
    ))
)

val conteudoFaq = listOf(
    FaqItem("O que é a Defensoria Pública?", "É uma instituição que presta assistência jurídica gratuita para quem não pode pagar um advogado."),
    FaqItem("Como peço uma medida protetiva?", "Você pode ir a uma Delegacia da Mulher ou a qualquer delegacia de polícia e registrar um boletim de ocorrência, solicitando as medidas protetivas."),
    FaqItem("Meu nome foi para o SPC/Serasa indevidamente. O que faço?", "Primeiro, contate a empresa para solicitar a retirada. Se não resolver, você pode procurar o Procon ou a Defensoria Pública."),
    FaqItem("Sofri um acidente de trabalho, quais meus direitos?", "Você tem direito à estabilidade no emprego por 12 meses após o retorno, além do auxílio-doença acidentário pago pelo INSS.")
)

val parceirosPrincipais = listOf(
    ParceiroPrincipal(Icons.Filled.AccountBalance, "Defensoria Pública", "Assistência jurídica gratuita", "129", "https://www.defensoria.sp.def.br/"),
    ParceiroPrincipal(Icons.Filled.Gavel, "Procon", "Defesa dos direitos do consumidor", "151", "https://www.procon.sp.gov.br/"),
    ParceiroPrincipal(Icons.Filled.Female, "Central de Atendimento à Mulher", "Orientação em situação de violência", "180", null),
    ParceiroPrincipal(Icons.Filled.Campaign, "Disque Direitos Humanos", "Denúncias de violações", "100", null),
    ParceiroPrincipal(Icons.Filled.AccountBalance, "Ministério Público", "Defesa dos direitos sociais", "127", "http://www.mpsp.mp.br/")
)

val outrosParceiros = listOf(
    "OAB - Ordem dos Advogados do Brasil",
    "CRAS - Centro de Referência de Assistência Social",
    "Casa da Mulher Brasileira",
    "Movimento Negro Unificado (MNU)",
    "ANTRA - Associação Nacional de Travestis e Transexuais",
    "Pastoral Carcerária",
    "Comissão de Direitos Humanos (Câmaras Municipais)"
)

// --- PREVIEWS ---
@Preview(showBackground = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun GlossarioScreenPreview() {
    AppTheme {
        GlossarioScreen(categoriasGlossario)
    }
}