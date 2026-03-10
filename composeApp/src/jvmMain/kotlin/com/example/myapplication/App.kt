@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.myapplication

import Pr22.FingerprintScannerDevice
import PrIns.Exceptions.General
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.kashif.cameraK.compose.rememberCameraKState
import com.kashif.cameraK.enums.AspectRatio
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.enums.QualityPrioritization
import com.kashif.cameraK.enums.TorchMode
import com.kashif.cameraK.state.CameraConfiguration
import com.kashif.cameraK.state.CameraKState
import com.kashif.qrscannerplugin.QRScanner
import com.kashif.qrscannerplugin.rememberQRScannerPlugin
import com.lowagie.text.Document
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.print.DocFlavor
import javax.print.PrintException
import javax.print.PrintServiceLookup
import javax.print.ServiceUI
import javax.print.SimpleDoc
import javax.print.attribute.HashPrintRequestAttributeSet

@Composable
@Preview
fun App() {

    val tempFile = File("print_output", ".pdf")


//    if (Desktop.isDesktopSupported()) {
//        val desktop = Desktop.getDesktop()
//        if (desktop.isSupported(Desktop.Action.PRINT)) {
//            desktop.print(tempFile)
//        }
//    }

//    printPdfFromMemory("some content")


    detectScanner()

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        if (showContent)
            ManualScannerScreen()
        else
            Button(onClick = { showContent = true }) {
                Text("Open camera")
            }
    }
}


fun printPdfFromMemory(content: String) {
    val out = ByteArrayOutputStream()
    val document = Document()
    PdfWriter.getInstance(document, out)
    document.open()
    document.add(Paragraph(content))
    document.add(Paragraph("Generated: ${java.time.LocalDateTime.now()}"))
    document.close()

    // 2. Convert RAM bytes to an InputStream
    val pdfData = out.toByteArray()
    val inputStream = ByteArrayInputStream(pdfData)

    val flavor = DocFlavor.INPUT_STREAM.AUTOSENSE
    val printService = PrintServiceLookup.lookupDefaultPrintService()
    val services = PrintServiceLookup.lookupPrintServices(flavor, null)
    val selectedService = ServiceUI.printDialog(
        null, 200, 200, services, printService, flavor, HashPrintRequestAttributeSet()
    )

    if (selectedService != null) {
        val job = selectedService.createPrintJob()
        val doc = SimpleDoc(inputStream, flavor, null)

        try {
            job.print(doc, HashPrintRequestAttributeSet())
        } catch (e: PrintException) {
            e.printStackTrace()
        }
    } else {
        println("No default printer found.")
    }
}

fun detectScanner(){
    try {
        val fps = FingerprintScannerDevice()

        val deviceList = FingerprintScannerDevice.getDeviceList()

        if (deviceList.isEmpty()) {
            println("No device found!")
            return
        }

        println("Found ${deviceList.size} device(s):")
        deviceList.forEach { println("  - $it") }

        println("\nConnecting to: ${deviceList[0]}")
        fps.useDevice(deviceList[0])
        println("Successfully connected!")

        fps.close()
        println("Device closed.")

    } catch (e: General) {
        println("Scanner error: ${e.message}")
    } catch (e: Exception) {
        println("General error: ${e.message}")
    }
}

@Composable
fun ManualScannerScreen() {
    val qrScanner = remember { QRScanner() }
    val qrScannerPlugin = rememberQRScannerPlugin()

    var cameraImage by remember { mutableStateOf<ImageBitmap?>(null) }

    var qrCodeContent by remember { mutableStateOf("") }

    val cameraState by rememberCameraKState(
        config = CameraConfiguration(
            aspectRatio = AspectRatio.RATIO_16_9,
            targetResolution = 1920 to 1080,
            flashMode = FlashMode.AUTO,
            torchMode = TorchMode.OFF,
            imageFormat = ImageFormat.JPEG,
            returnFilePath = true,
            qualityPrioritization = QualityPrioritization.BALANCED,
        ),
        setupPlugins = { stateHolder ->
            stateHolder.attachPlugin(qrScannerPlugin)
        },
    )

    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = qrCodeContent)
    }

    scope.launch(Dispatchers.IO) {
        when (cameraState) {
            is CameraKState.Error -> {
                println("error: ${(cameraState as CameraKState.Error).message}")
            }

            CameraKState.Initializing -> {
                println("Initializing")
            }

            is CameraKState.Ready -> {
                val frameChannel = (cameraState as CameraKState.Ready).controller.getFrameChannel()
                while (true) {
                    runCatching {
                        val bufferedImage = frameChannel.receive()

                        while (!frameChannel.isEmpty) {
                            frameChannel.tryReceive()
                        }

                        cameraImage = bufferedImage.toComposeImageBitmap()
                        val result = qrScanner.scanImage(bufferedImage)

                        result?.let {
                            qrCodeContent = it
                        }

                    }.onFailure {
                        println("error $it")
                    }
                }
            }
        }
    }

    if (qrCodeContent.isNotEmpty())
        println("qrCodeContent: $qrCodeContent")


    Box {
        when (cameraState) {
            is CameraKState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Loading the camera..")
                }
            }

            CameraKState.Initializing -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Loading the camera..")
                }
            }

            is CameraKState.Ready -> cameraImage?.let {
                Box(Modifier.fillMaxSize()) {
                    Image(bitmap = it, modifier = Modifier.fillMaxSize(), contentDescription = null)
                }
            }
        }
    }
}

fun generatePdf(fileName: String, content: String) {
    val document = Document()
    try {
        PdfWriter.getInstance(document, FileOutputStream(fileName))
        document.open()
        document.addTitle("Title")
        document.addKeywords("Keywords")

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        document.close()
    }
}
