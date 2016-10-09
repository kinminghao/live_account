package org.kin.live.live_account.action;

import org.kin.live.live_account.dao.TransExtMapper;
import org.kin.live.live_account.dao.TransMapper;
import org.kin.live.live_account.domain.Trans;
import org.kin.live.live_account.domain.TransExt;
import org.kin.live.live_account.except.BaseException;
import org.kin.live.live_account.service.ValidateService;
import org.kin.live.live_account.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by kingsir on 16-9-26.
 */
@Controller
@RequestMapping("/trans")
public class TransAction {

    @Resource
    private ValidateService validateService;

    @Resource
    private TransMapper transMapper;
    @Resource
    private TransExtMapper transExtMapper;

    @RequestMapping("/newTrans")
    public String newTrans(HttpServletRequest request) throws BaseException{
        String payTime = request.getParameter("payTime");
        System.out.println(payTime);
        validateService.transParamsValidate(request);

        this.saveTrans(request);

        return "aaa";
    }

    private void saveTrans(HttpServletRequest request){
        String payTimeStr = request.getParameter("payTime");
        String transAmtStr = request.getParameter("transAmt");
        String payerId = request.getParameter("payer");
        String shares = request.getParameter("shares");
        String memo = request.getParameter("memo");
        String groupId = request.getParameter("groupId");

        try {
            Date payTime = DateUtil.getDate(payTimeStr,DateUtil.DATE_FORMAT_SHORT);
            BigDecimal transAmt = new BigDecimal(transAmtStr).multiply(new BigDecimal(100));
            String[] shareArray = shares.split(",");

            Trans trans = new Trans();
            trans.setDivideTo(shareArray.length);
            trans.setGroupId(groupId);
            trans.setMemo(memo);
            trans.setPayerId(payerId);
            trans.setPayTime(payTime);
            trans.setPerAmt(transAmt.divide(new BigDecimal(shareArray.length)));
            trans.setTransAmt(transAmt);
            transMapper.insertSelective(trans);

            for(String share : shareArray){
                TransExt ext = new TransExt();
                ext.setGroupId(groupId);
                ext.setTransId(trans.getId());
                ext.setUserId(share);
                transExtMapper.insertSelective(ext);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
