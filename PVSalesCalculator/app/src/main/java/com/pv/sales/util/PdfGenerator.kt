package com.pv.sales.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.pv.sales.model.CalculationResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PDF报告生成器
 * 使用iText7生成专业光伏测算报告
 */
object PdfGenerator {

    private const val PRIMARY_BLUE = 0x15.toByte()
    private const val GREEN = 0x2E.toByte()

    /**
     * 生成PDF报告
     */
    fun generate(context: Context, result: CalculationResult): File {
        val fileName = "光伏测算报告_${result.province}${result.city}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())}.pdf"
        val downloadsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        } else {
            @Suppress("DEPRECATION")
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

        val outputFile = File(downloadsDir, fileName)
        outputFile.parentFile?.mkdirs()

        val writer = PdfWriter(FileOutputStream(outputFile))
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        try {
            // 尝试加载中文字体
            val font = try {
                PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true)
            } catch (e: Exception) {
                try {
                    PdfFontFactory.createFont("/system/fonts/NotoSansCJK-Regular.ttc,0", true)
                } catch (e2: Exception) {
                    PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false)
                }
            }

            val blueColor = DeviceRgb(0x15, 0x65, 0xC0)
            val greenColor = DeviceRgb(0x2E, 0x7D, 0x32)
            val grayColor = DeviceRgb(0x75, 0x75, 0x75)

            // === 标题 ===
            document.add(Paragraph("光伏发电项目测算报告")
                .setFont(font)
                .setFontSize(24f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5f))

            document.add(Paragraph("PV Solar Power Calculation Report")
                .setFont(font)
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(grayColor)
                .setMarginBottom(20f))

            // 分割线
            document.add(LineSeparator(DeviceRgb(0x15, 0x65, 0xC0)).setMarginBottom(15f))

            // === 项目概况 ===
            document.add(Paragraph("一、项目概况")
                .setFont(font)
                .setFontSize(14f)
                .setBold()
                .setFontColor(blueColor)
                .setMarginBottom(8f))

            addInfoTable(document, font, listOf(
                "项目地区" to "${result.province} ${result.city}",
                "安装场景" to result.scene,
                "屋顶面积" to "${String.format("%.1f", result.roofArea)} ㎡",
                "发电模式" to result.generationMode,
                "报告日期" to SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(Date())
            ))

            // === 装机方案 ===
            document.add(Paragraph("\n二、装机方案")
                .setFont(font)
                .setFontSize(14f)
                .setBold()
                .setFontColor(blueColor)
                .setMarginBottom(8f))

            addInfoTable(document, font, listOf(
                "装机容量" to "${String.format("%.1f", result.installedCapacity)} kW",
                "组件数量" to "${result.panelCount} 块",
                "组件功率" to "${result.panelPower} W",
                "系统效率" to "${String.format("%.0f", result.systemEfficiency * 100)}%",
                "安装倾角" to "${String.format("%.1f", result.tiltAngle)}°"
            ))

            // === 发电量预测 ===
            document.add(Paragraph("\n三、发电量预测")
                .setFont(font)
                .setFontSize(14f)
                .setBold()
                .setFontColor(blueColor)
                .setMarginBottom(8f))

            addInfoTable(document, font, listOf(
                "日均发电量" to "${String.format("%.1f", result.dailyGeneration)} kWh",
                "月均发电量" to "${String.format("%.0f", result.monthlyGeneration)} kWh",
                "年发电量" to "${String.format("%.0f", result.yearlyGeneration)} kWh",
                "25年总发电量" to "${String.format("%.0f", result.total25yGeneration)} kWh"
            ))

            // === 投资收益分析 ===
            document.add(Paragraph("\n四、投资收益分析")
                .setFont(font)
                .setFontSize(14f)
                .setBold()
                .setFontColor(blueColor)
                .setMarginBottom(8f))

            addInfoTable(document, font, listOf(
                "项目总投资" to "¥${String.format("%.2f", result.totalInvestment)}",
                "月节电收益" to "¥${String.format("%.2f", result.monthlySaving)}",
                "年节电收益" to "¥${String.format("%.2f", result.yearlySaving)}",
                "25年总收益" to "¥${String.format("%.2f", result.total25ySaving)}",
                "静态回本周期" to "${String.format("%.1f", result.staticPayback)} 年",
                "动态回本周期" to "${String.format("%.1f", result.dynamicPayback)} 年"
            ))

            // === 工厂用电分析（如有）===
            if (result.coverageRatio > 0) {
                document.add(Paragraph("\n五、工厂用电分析")
                    .setFont(font)
                    .setFontSize(14f)
                    .setBold()
                    .setFontColor(blueColor)
                    .setMarginBottom(8f))

                addInfoTable(document, font, listOf(
                    "用电覆盖比例" to "${String.format("%.1f", result.coverageRatio)}%",
                    "节电率" to "${String.format("%.1f", result.savingRate)}%"
                ))
            }

            // === 环保贡献 ===
            document.add(Paragraph("\n${if (result.coverageRatio > 0) "六" else "五"}、环保贡献")
                .setFont(font)
                .setFontSize(14f)
                .setBold()
                .setFontColor(greenColor)
                .setMarginBottom(8f))

            addInfoTable(document, font, listOf(
                "25年碳减排量" to "${String.format("%.1f", result.carbonReduction)} 吨CO₂",
                "等效植树量" to "${String.format("%.0f", result.treesEquivalent)} 棵"
            ))

            // === 25年收益趋势 ===
            val sectionNum = if (result.coverageRatio > 0) "七" else "六"
            document.add(Paragraph("\n${sectionNum}、25年收益趋势")
                .setFont(font)
                .setFontSize(14f)
                .setBold()
                .setFontColor(blueColor)
                .setMarginBottom(8f))

            // 简易表格展示逐年数据
            val table = com.itextpdf.layout.element.Table(
                UnitValue.createPercentArray(floatArrayOf(15f, 25f, 25f, 35f))
            ).setWidth(UnitValue.createPercentValue(100f))

            table.addHeaderCell(
                Cell().add(Paragraph("年份").setFont(font).setFontSize(9f).setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(DeviceRgb(0x15, 0x65, 0xC0))
                    .setFontColor(DeviceRgb(255, 255, 255))
            )
            table.addHeaderCell(
                Cell().add(Paragraph("年发电量(kWh)").setFont(font).setFontSize(9f).setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(DeviceRgb(0x15, 0x65, 0xC0))
                    .setFontColor(DeviceRgb(255, 255, 255))
            )
            table.addHeaderCell(
                Cell().add(Paragraph("年收益(元)").setFont(font).setFontSize(9f).setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(DeviceRgb(0x15, 0x65, 0xC0))
                    .setFontColor(DeviceRgb(255, 255, 255))
            )
            table.addHeaderCell(
                Cell().add(Paragraph("累计收益(元)").setFont(font).setFontSize(9f).setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(DeviceRgb(0x15, 0x65, 0xC0))
                    .setFontColor(DeviceRgb(255, 255, 255))
            )

            result.yearlyData.forEach { data ->
                val bgColor = if (data.year % 2 == 0) DeviceRgb(0xF5, 0xF5, 0xF5) else DeviceRgb(255, 255, 255)
                table.addCell(Cell().add(Paragraph("第${data.year}年").setFont(font).setFontSize(8f))
                    .setTextAlignment(TextAlignment.CENTER).setBackgroundColor(bgColor))
                table.addCell(Cell().add(Paragraph(String.format("%.0f", data.generation)).setFont(font).setFontSize(8f))
                    .setTextAlignment(TextAlignment.CENTER).setBackgroundColor(bgColor))
                table.addCell(Cell().add(Paragraph(String.format("%.2f", data.saving)).setFont(font).setFontSize(8f))
                    .setTextAlignment(TextAlignment.CENTER).setBackgroundColor(bgColor))
                table.addCell(Cell().add(Paragraph(String.format("%.2f", data.cumulativeSaving)).setFont(font).setFontSize(8f))
                    .setTextAlignment(TextAlignment.CENTER).setBackgroundColor(bgColor))
            }
            document.add(table)

            // === 免责声明 ===
            document.add(Paragraph("\n")
                .setFont(font).setFontSize(6f))
            document.add(Paragraph("免责声明：本报告测算结果仅供参考，实际发电量和收益受天气、设备质量、安装条件等因素影响。具体方案请以实际勘察和设计为准。")
                .setFont(font)
                .setFontSize(7f)
                .setFontColor(grayColor)
                .setTextAlignment(TextAlignment.CENTER))

            document.close()
        } catch (e: Exception) {
            document.close()
            throw e
        }

        return outputFile
    }

    private fun addInfoTable(document: Document, font: com.itextpdf.kernel.font.PdfFont, items: List<Pair<String, String>>) {
        val table = com.itextpdf.layout.element.Table(
            UnitValue.createPercentArray(floatArrayOf(35f, 65f))
        ).setWidth(UnitValue.createPercentValue(100f))

        items.forEach { (label, value) ->
            table.addCell(
                Cell().add(Paragraph(label).setFont(font).setFontSize(10f))
                    .setBackgroundColor(DeviceRgb(0xF0, 0xF0, 0xF0))
                    .setPadding(5f)
            )
            table.addCell(
                Cell().add(Paragraph(value).setFont(font).setFontSize(10f))
                    .setPadding(5f)
            )
        }
        document.add(table)
    }

    /**
     * 分享PDF文件
     */
    fun sharePdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "分享PDF报告"))
        } catch (e: Exception) {
            Toast.makeText(context, "分享失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
