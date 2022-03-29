package com.xck.model;

import cn.hutool.core.io.FileUtil;
import com.xck.util.StrUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单模板列表
 *
 * @author xuchengkun
 * @date 2022/03/26 15:53
 **/
@Component
public class FormTempleteList {

    private final static String formTemplateCSV = System.getProperty("user.dir") + "/config/formTemplate.csv";

    private static FormTemplate[] formTemplates;

    @PostConstruct
    public void init() {
        List<String> templeteList = FileUtil.readUtf8Lines(formTemplateCSV);

        formTemplates = new FormTemplate[1000];

        for (int i = 0; i < templeteList.size(); i++) {
            String line = templeteList.get(i);
            String[] fieldArr = line.split("&");
            FormTemplate formTemplate = new FormTemplate();
            formTemplate.setId(Integer.parseInt(fieldArr[0]));
            formTemplate.setProcessonName(fieldArr[1]);
            formTemplate.setTempleteName(fieldArr[2]);
            formTemplate.setUri(fieldArr[3]);
            formTemplate.setRule(StrUtils.json2Obj(fieldArr[4]));

            formTemplates[formTemplate.getId()] = formTemplate;
        }
    }

    public List<TemplateMenu> templateMenus() {
        List<TemplateMenu> list = new ArrayList<>();

        TemplateMenu templateMenu = null;
        for (int i = 0; i < formTemplates.length; i++) {
            FormTemplate formTemplate = formTemplates[i];
            if (formTemplate == null) continue;

            if(templateMenu == null){
                templateMenu = new TemplateMenu();
                templateMenu.setId(i);
                templateMenu.setMenuName(formTemplate.getProcessonName());
                list.add(templateMenu);
            } else if (!formTemplate.getProcessonName().equals(templateMenu.getMenuName())) {
                templateMenu = new TemplateMenu();
                templateMenu.setId(i);
                templateMenu.setMenuName(formTemplate.getProcessonName());
                list.add(templateMenu);
            }

            TemplateMenuItem templateMenuItem = new TemplateMenuItem();
            templateMenuItem.setId(formTemplate.getId());
            templateMenuItem.setTempleteName(formTemplate.getTempleteName());

            templateMenu.addChildren(templateMenuItem);
        }

        return list;
    }

    public boolean isTemplateExists(int templateId) {
        if (templateId < 0 || templateId >= formTemplates.length) return false;
        return formTemplates[templateId] != null;
    }

    public FormTemplate templateByIndex(int templateId) {
        if (isTemplateExists(templateId)) {
            return formTemplates[templateId];
        }

        return null;
    }

    public static class TemplateMenu {
        private int id;
        private String menuName;
        private List<TemplateMenuItem> children;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }

        public List<TemplateMenuItem> getChildren() {
            return children;
        }

        public void setChildren(List<TemplateMenuItem> children) {
            this.children = children;
        }

        public void addChildren(TemplateMenuItem item){
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(item);
        }
    }

    public static class TemplateMenuItem {
        private int id;
        private String templeteName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTempleteName() {
            return templeteName;
        }

        public void setTempleteName(String templeteName) {
            this.templeteName = templeteName;
        }
    }

    public static class FormTemplate {
        private int id;
        private String processonName;
        private String templeteName;
        private String uri;
        private Object rule;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProcessonName() {
            return processonName;
        }

        public void setProcessonName(String processonName) {
            this.processonName = processonName;
        }

        public String getTempleteName() {
            return templeteName;
        }

        public void setTempleteName(String templeteName) {
            this.templeteName = templeteName;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public Object getRule() {
            return rule;
        }

        public void setRule(Object rule) {
            this.rule = rule;
        }
    }
}
