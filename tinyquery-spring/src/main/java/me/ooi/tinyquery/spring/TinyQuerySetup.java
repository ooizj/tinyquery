package me.ooi.tinyquery.spring;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import me.ooi.tinyquery.util.ScanUtils;

/**
 * &lt;bean class="me.ooi.tinyquery.spring.SpringConfiguration" init-method="init" scope="singleton"><br>
 * 		&lt;property name="dataSource" ref="dataSource" /><br>
 * 	&lt;/bean><br>
 * &lt;!-- sacanPackage: split by comma --><br>
 * 	&lt;bean class="me.ooi.tinyquery.spring.TinyQuerySetup" scope="singleton"><br>
 * 		&lt;property name="sacanPackage" value="xxx" /><br>
 * 	&lt;/bean>
 * @author jun.zhao
 */
public class TinyQuerySetup implements BeanFactoryPostProcessor{
	
	private String sacanPackage;
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] sacanPackages = sacanPackage.split(",");
		for (String sacanPackage : sacanPackages) {
			postProcessBeanFactory(beanFactory, sacanPackage);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, String packageName) throws BeansException {
		if( StringUtils.isEmpty(packageName) ) {
			return;
		}
		
		List<Class> queryInterfaces = ScanUtils.findInterface(packageName) ;
		for (Class queryInterface: queryInterfaces) {
			GenericBeanDefinition bd = new GenericBeanDefinition();
			bd.setBeanClass(QueryFactoryBean.class);
			bd.getPropertyValues().addPropertyValue("queryInterface", queryInterface);
			((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(beanName(queryInterface), bd);
		}
	} 
	
	private String beanName(Class<?> queryInterface) {
		return StringUtils.uncapitalize(queryInterface.getName());
	}

	public String getSacanPackage() {
		return sacanPackage;
	}

	public void setSacanPackage(String sacanPackage) {
		this.sacanPackage = sacanPackage;
	}

}
