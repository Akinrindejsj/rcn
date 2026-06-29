package com.example.rcn.flowable;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * Task listener that fires when the editor review task is created. In a full
 * deployment this would email the rcn-editors group; here it simply logs the
 * pending review so the queue is visible in the CMS.
 */
public class NotifyEditorsDelegate implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        Long articleId = (Long) delegateTask.getVariable("articleId");
        String title = (String) delegateTask.getVariable("articleTitle");
        // Hook for future email/notification integration.
        System.out.println("[RCN] Article pending review (id=" + articleId
                + "): \"" + title + "\" — task " + delegateTask.getId());
    }
}
