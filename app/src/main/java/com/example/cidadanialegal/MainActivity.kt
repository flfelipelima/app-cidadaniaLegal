package com.example.cidadanialegal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

// --- Estrutura da Aplicação (Simples) ---
object Routes {
    const val HOME = "home"
    const val MEUS_DIREITOS = "meus_direitos"
    const val TIRA_DUVIDAS = "tira_duvidas"
    const val GERADOR_DOCS = "gerador_docs"
    const val FAQ = "faq"
    const val DENUNCIA = "denuncia"
    const val PARCEIROS = "parceiros"
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
        background = Color(0xFFFDFBF8),
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF333333),
        onSurface = Color(0xFF333333),
        error = Color(0xFFB00020)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp)
        ),
        content = content
    )
}

// --- Componente Principal com Navegação ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CidadaniaLegalApp() {
    val navController = rememberNavController()
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
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.HOME) { HomeScreen(navController) }
                composable(Routes.MEUS_DIREITOS) { MeusDireitosScreen() }
                composable(Routes.TIRA_DUVIDAS) { TiraDuvidasScreen() }
                composable(Routes.GERADOR_DOCS) { GeradorDocumentosScreen() }
                composable(Routes.FAQ) { FaqScreen() }
                composable(Routes.DENUNCIA) { DenunciaScreen() }
                composable(Routes.PARCEIROS) { ParceirosScreen() }
            }
        }
    }
}


// --- Ecrãs da Aplicação ---

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Seu guia de direitos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            "Uma ferramenta para capacitar o cidadão a dar o primeiro passo na sua jornada legal.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        FeatureButton(navController, Routes.MEUS_DIREITOS, "Meus Direitos", Icons.Filled.Gavel, "Conheça seus direitos básicos.")
        FeatureButton(navController, Routes.TIRA_DUVIDAS, "Tira-Dúvidas (IA)", Icons.Filled.QuestionAnswer, "Pergunte ao nosso assistente virtual.")
        FeatureButton(navController, Routes.GERADOR_DOCS, "Gerador de Documentos (IA)", Icons.Filled.Description, "Crie rascunhos de documentos.")
        FeatureButton(navController, Routes.FAQ, "Perguntas Frequentes", Icons.Filled.Quiz, "Veja as dúvidas mais comuns.")
        FeatureButton(navController, Routes.PARCEIROS, "Parceiros e Apoio", Icons.Filled.People, "Encontre ajuda de instituições.")
        FeatureButton(navController, Routes.DENUNCIA, "Denúncia", Icons.Filled.Report, "Registre violações de direitos.", isDestructive = true)
    }
}

@Composable
fun FeatureButton(navController: NavController, route: String, title: String, icon: ImageVector, subtitle: String, isDestructive: Boolean = false) {
    val backgroundColor = if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val iconColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate(route) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}


@Composable
fun MeusDireitosScreen() {
    val direitos = listOf(
        "Consumidor" to "Direitos em cobranças, garantias, SPC/Serasa.",
        "Trabalhista" to "Salário-família, acidentes de trabalho, direitos básicos.",
        "Moradia" to "Direitos de inquilinos, Aluguel Social.",
        "Violência Doméstica" to "Tipos de violência e como buscar proteção."
    )
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(direitos) { (titulo, descricao) ->
            InfoCard(titulo = titulo, descricao = descricao)
        }
    }
}

@Composable
fun InfoCard(titulo: String, descricao: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(descricao, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

data class Mensagem(val texto: String, val eDoUsuario: Boolean)

@Composable
fun TiraDuvidasScreen() {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val mensagens = remember { mutableStateListOf(Mensagem("Olá! Como posso ajudar a entender seus direitos hoje?", false)) }
    var estaAProcessar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mensagens) { msg ->
                MessageBubble(msg)
            }
        }
        if (estaAProcessar) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Digite sua pergunta...") },
                enabled = !estaAProcessar
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (input.text.isNotBlank()) {
                        mensagens.add(Mensagem(input.text, true))
                        input = TextFieldValue("")
                        estaAProcessar = true

                        scope.launch {
                            delay(2000)
                            mensagens.add(Mensagem("Lembre-se: esta é uma orientação informativa e não substitui um advogado. Para o seu caso, é fundamental procurar a Defensoria Pública.", false))
                            estaAProcessar = false
                        }
                    }
                },
                enabled = !estaAProcessar && input.text.isNotBlank(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Enviar")
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
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (mensagem.eDoUsuario) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = mensagem.texto,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeradorDocumentosScreen() {
    var descricao by remember { mutableStateOf("") }
    var tipoDocumento by remember { mutableStateOf("E-mail Formal") }
    var documentoGerado by remember { mutableStateOf<String?>(null) }
    var estaAGerar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Gerador de Documentos", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descreva seu problema") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = tipoDocumento,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Documento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("E-mail Formal", "Carta de Reclamação", "Modelo de Reclamação").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = {
                        tipoDocumento = it
                        expanded = false
                    })
                }
            }
        }

        Button(
            onClick = {
                if(descricao.isNotBlank()) {
                    estaAGerar = true
                    scope.launch {
                        delay(1500)
                        documentoGerado = """
Prezados(as),

Eu, [Nome Completo], CPF [Seu CPF], venho por meio deste documento registrar a seguinte questão:

$descricao

Diante do exposto, solicito uma solução.
Aguardando retorno.

Atenciosamente,
[Seu Nome Completo]
[Sua Cidade], [Data]
                        """.trimIndent()
                        estaAGerar = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaAGerar
        ) {
            if (estaAGerar) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Gerar Rascunho")
            }
        }

        AnimatedVisibility(visible = documentoGerado != null, enter = fadeIn(), exit = fadeOut()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Rascunho Gerado:", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(documentoGerado ?: "", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


@Composable
fun FaqScreen() {
    val faqs = remember {
        listOf(
            "O que é a Defensoria Pública?" to "É uma instituição que presta assistência jurídica gratuita para quem não pode pagar um advogado.",
            "Como peço uma medida protetiva?" to "Você pode ir a uma Delegacia da Mulher ou a qualquer delegacia de polícia e registrar um boletim de ocorrência, solicitando as medidas protetivas.",
            "Meu nome foi para o SPC/Serasa indevidamente. O que faço?" to "Primeiro, contate a empresa para solicitar a retirada. Se não resolver, você pode procurar o Procon ou a Defensoria Pública.",
            "Sofri um acidente de trabalho, quais meus direitos?" to "Você tem direito à estabilidade no emprego por 12 meses após o retorno, além do auxílio-doença acidentário pago pelo INSS."
        )
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(faqs) { (pergunta, resposta) ->
            FaqItem(pergunta, resposta)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun FaqItem(pergunta: String, resposta: String) {
    var expandido by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expandido = !expandido },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pergunta, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expandido) "Recolher" else "Expandir"
                )
            }
            AnimatedVisibility(visible = expandido, enter = fadeIn(animationSpec = tween(300)), exit = fadeOut(animationSpec = tween(300))) {
                Text(
                    text = resposta,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 12.dp)
                )
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
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha=0.1f))) {
                Column(Modifier.padding(16.dp)) {
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

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descreva a violação") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = tipoViolacao,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Violação") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Discriminação", "Violência Física/Psicológica", "Abuso de Autoridade", "Outro").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            tipoViolacao = it
                            expanded = false
                        })
                    }
                }
            }

            Button(
                onClick = {
                    denunciaEnviada = true
                    descricao = ""
                },
                modifier = Modifier.fillMaxWidth(),
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

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(parceiros) { (nome, descricao) ->
            InfoCard(titulo = nome, descricao = descricao)
        }
    }
}


// --- Previews para Desenvolvimento no Android Studio ---
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun TiraDuvidasPreview() {
    AppTheme {
        TiraDuvidasScreen()
    }
}

