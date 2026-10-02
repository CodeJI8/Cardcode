package com.singleminds.cardcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

val AppBg = Color(0xFF14110F)
val AppText = Color(0xFFF4EBDD)
val AppPrimary = Color(0xFFF2A93B)
val AppSurface = Color(0xFF1E1A18)
val AppBorder = Color(0xFF332D29)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = AppBg,
            surface = AppSurface,
            primary = AppPrimary,
            onBackground = AppText,
            onSurface = AppText,
            outline = AppBorder
        ),
        typography = Typography(
            headlineMedium = androidx.compose.material3.Typography().headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            ),
            bodyLarge = androidx.compose.material3.Typography().bodyLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp
            )
        ),
        content = content
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppSurface,
            unfocusedContainerColor = AppSurface,
            focusedBorderColor = AppPrimary,
            unfocusedBorderColor = AppBorder,
            focusedLabelColor = AppPrimary,
            unfocusedLabelColor = AppText.copy(alpha = 0.7f)
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                TextButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.semantics { contentDescription = "Toggle password visibility" }
                ) {
                    Text(if (passwordVisible) "Hide" else "Show", color = AppPrimary)
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppPrimary,
            contentColor = AppBg,
            disabledContainerColor = AppBorder,
            disabledContentColor = AppText.copy(alpha = 0.5f)
        )
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PickerScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Choose a template", style = MaterialTheme.typography.headlineMedium, color = AppText)

        TemplateCard("Wi-Fi", "Share your network easily", 0) { viewModel.selectTemplate(Template.Wifi) }
        TemplateCard("Link", "Direct to a website", 1) { viewModel.selectTemplate(Template.Link) }
        TemplateCard("Contact", "Share your details", 2) { viewModel.selectTemplate(Template.Contact) }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun TemplateCard(title: String, desc: String, index: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 120L)
        visible = true
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(stiffness = Spring.StiffnessMediumLow), label = "cardScale")

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { 60 }, animationSpec = spring(dampingRatio = 0.7f)) + fadeIn(tween(400))
    ) {
        Card(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .scale(scale)
                .border(1.dp, Brush.linearGradient(listOf(Color(0x40FFFFFF), Color.Transparent)), RoundedCornerShape(24.dp))
                .semantics { contentDescription = "Template $title" },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.radialGradient(listOf(Color(0xFF2A2420), AppSurface), radius = 600f))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = AppText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(desc, fontSize = 16.sp, color = AppText.copy(alpha = 0.65f))
                }
            }
        }
    }
}

@Composable
fun FormScreen(state: AppState, viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Enter details", style = MaterialTheme.typography.headlineMedium, color = AppText)

        when (state.template) {
            Template.Wifi -> {
                CustomTextField(state.wifiSsid, { viewModel.updateState { s -> s.copy(wifiSsid = it) } }, "Network Name (SSID)")
                CustomTextField(state.wifiPass, { viewModel.updateState { s -> s.copy(wifiPass = it) } }, "Password", isPassword = true)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("WPA", "WEP", "nopass").forEach { sec ->
                        FilterChip(
                            selected = state.wifiSecurity == sec,
                            onClick = { viewModel.updateState { s -> s.copy(wifiSecurity = sec) } },
                            label = { Text(if (sec == "nopass") "None" else sec, fontSize = 16.sp) },
                            modifier = Modifier.height(48.dp).semantics { contentDescription = "Security $sec" },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppPrimary,
                                selectedLabelColor = AppBg,
                                containerColor = AppSurface,
                                labelColor = AppText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = state.wifiSecurity == sec,
                                borderColor = if (state.wifiSecurity == sec) AppPrimary else AppBorder
                            )
                        )
                    }
                }
            }
            Template.Link -> {
                CustomTextField(state.linkUrl, { viewModel.updateState { s -> s.copy(linkUrl = it) } }, "URL (e.g. example.com)")
                CustomTextField(state.linkLabel, { viewModel.updateState { s -> s.copy(linkLabel = it) } }, "Label (optional)")
            }
            Template.Contact -> {
                CustomTextField(state.contactName, { viewModel.updateState { s -> s.copy(contactName = it) } }, "Name")
                CustomTextField(state.contactPhone, { viewModel.updateState { s -> s.copy(contactPhone = it) } }, "Phone")
                CustomTextField(state.contactEmail, { viewModel.updateState { s -> s.copy(contactEmail = it) } }, "Email")
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        
        val isValid = when (state.template) {
            Template.Wifi -> state.wifiSsid.isNotBlank()
            Template.Link -> state.linkUrl.isNotBlank()
            Template.Contact -> state.contactName.isNotBlank() || state.contactPhone.isNotBlank() || state.contactEmail.isNotBlank()
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 24.dp)) {
            OutlinedButton(
                onClick = { viewModel.updateState { it.copy(screen = ScreenState.Picker) } },
                modifier = Modifier.weight(1f).height(56.dp).semantics { contentDescription = "Go back" },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppText),
                border = BorderStroke(1.dp, AppBorder)
            ) { Text("Back", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            
            AppButton(
                onClick = { viewModel.updateState { it.copy(screen = ScreenState.Studio) } },
                text = "Continue",
                enabled = isValid,
                modifier = Modifier.weight(2f).semantics { contentDescription = "Continue to preview" }
            )
        }
    }
}

@Composable
fun StudioScreen(state: AppState, viewModel: MainViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isReady = true }

    val scale by animateFloatAsState(
        targetValue = if (isReady) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isReady) 0f else -6f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "rotation"
    )

    val sub = if (state.subtitle.isNotBlank()) state.subtitle else {
        when (state.template) {
            Template.Wifi -> {
                val passPart = if (state.showPasswordOnCard && state.wifiSecurity != "nopass" && state.wifiPass.isNotBlank()) "\nPass: ${state.wifiPass}" else ""
                "${state.wifiSsid}$passPart"
            }
            Template.Link -> state.linkLabel.takeIf { it.isNotBlank() } ?: state.linkUrl
            Template.Contact -> state.contactName
        }
    }

    val bitmap by produceState<Bitmap?>(initialValue = null, state.theme, state.headline, sub, viewModel.getPayload()) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            CardRenderer.render(state.theme, viewModel.getPayload(), state.headline, sub)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AppBg).padding(top = 48.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = bitmap, label = "card_crossfade", animationSpec = tween(400)) { b ->
                b?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Card Preview",
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                rotationZ = rotation
                                shadowElevation = 32.dp.toPx()
                                shape = RoundedCornerShape(24.dp)
                                clip = true
                            }
                            .fillMaxSize()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppSurface, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(state.headline, { viewModel.updateState { s -> s.copy(headline = it) } }, "Headline")
            CustomTextField(state.subtitle, { viewModel.updateState { s -> s.copy(subtitle = it) } }, "Subtitle (optional)")

            if (state.template == Template.Wifi && state.wifiSecurity != "nopass") {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Show password on card", modifier = Modifier.weight(1f), color = AppText)
                    Switch(
                        checked = state.showPasswordOnCard,
                        onCheckedChange = { viewModel.updateState { s -> s.copy(showPasswordOnCard = it) } },
                        modifier = Modifier.semantics { contentDescription = "Toggle password on card" },
                        colors = SwitchDefaults.colors(checkedThumbColor = AppBg, checkedTrackColor = AppPrimary, uncheckedThumbColor = AppText, uncheckedTrackColor = AppBorder)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                CardTheme.values().forEach { theme ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(theme.bg))
                            .border(
                                2.dp,
                                if (state.theme == theme) AppPrimary else Color.Transparent,
                                CircleShape
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateState { s -> s.copy(theme = theme) }
                            }
                            .semantics { contentDescription = "Theme ${theme.name}" }
                    ) {
                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(theme.accent)).align(Alignment.Center))
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { viewModel.updateState { it.copy(screen = ScreenState.Form) } },
                    modifier = Modifier.weight(1f).height(56.dp).semantics { contentDescription = "Edit details" },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppText),
                    border = BorderStroke(1.dp, AppBorder)
                ) { Text("Edit", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

                AppButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        bitmap?.let { b -> shareBitmap(context, b) }
                    },
                    text = "Share",
                    modifier = Modifier.weight(2f).semantics { contentDescription = "Share card as image" }
                )
            }
        }
    }
}

fun shareBitmap(context: Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "shared_images")
        if (!cachePath.exists()) cachePath.mkdirs()
        val file = File(cachePath, "cardcode.png")
        if (file.exists()) file.delete()
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Card"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Crossfade(targetState = state.screen, label = "screen_transition") { screen ->
                when (screen) {
                    ScreenState.Picker -> PickerScreen(viewModel)
                    ScreenState.Form -> FormScreen(state, viewModel)
                    ScreenState.Studio -> StudioScreen(state, viewModel)
                }
            }
        }
    }
}
