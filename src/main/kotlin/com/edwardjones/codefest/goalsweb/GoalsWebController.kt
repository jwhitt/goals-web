package com.edwardjones.codefest.goalsweb

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ModelAndView
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import com.edwardjones.financialassessment.calculators.contributionanalyzer.ContributionAnalysisSummary
import com.edwardjones.financialassessment.calculators.contributionanalyzer.ContributionScenarioGoalResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.util.*


@Controller
class GoalsWebController {

    @Value("\${contribution.svc.url}")
    val svcUrl : String = "http://localhost:8090/calc"

    @GetMapping("/")
    fun homePage() : ModelAndView {
        var mod = ModelAndView("goals")
        mod.model.put("amount", "0")
        mod.model.put("summary", ArrayList<GoalView>())
        mod.model.put("header", true)
        return mod
    }

    @GetMapping("/good")
    fun goodHomePage() : ModelAndView {
        var mod = ModelAndView("goals")
        mod.model.put("amount", "0")
        mod.model.put("summary", ArrayList<GoalView>())
        mod.model.put("header", false)
        return mod
    }

    @PostMapping("/calc-submit")
    fun calcPage(@RequestParam amount: String, @RequestParam header: Boolean) : ModelAndView {
        var mod = ModelAndView("goals")
        var rt  = RestTemplate()

        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        if (header) {
            headers.set("end-user", "jason")
        }
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))

        val entity = HttpEntity<CalcInput>(CalcInput(amount.toDouble()), headers)

        val summary = rt.postForEntity(svcUrl, entity, ContributionAnalysisSummary::class.java)
        mod.model.put("amount", amount)
        mod.model.put("summary", buildViewObj(summary.body))
        return mod
    }

    fun buildViewObj(summary:ContributionAnalysisSummary?) : List<GoalView> {
        var goalViewList : List<GoalView>;
        goalViewList = ArrayList()
        for (beforeOut in summary?.before!!.goalResults) {
            println("beforeOut=${beforeOut.goalName}")
            for (afterOut in summary?.after!!.goalResults) {
                println("afterOut=${afterOut.goalName}")

                if(beforeOut != null && beforeOut.goalName != null && beforeOut.goalName.equals(afterOut.goalName)) {
                    goalViewList.add(
                            GoalView(beforeOut.goalName,
                                    beforeOut.successRate,
                                    afterOut.successRate,
                                    afterOut.contribAmt))
                    continue
                }
            }

        }
        return goalViewList
    }
}

