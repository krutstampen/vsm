//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.15 at 09:16:57 AM CEST 
//

package com.cybercom.svp.machine.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="required-parameters">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="parameter" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="extra-parameters">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="parameter" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="time" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="api-version" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="request-id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="volatile" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "requiredParameters", "extraParameters" })
@XmlRootElement(name = "event")
public class MachineEvent implements Serializable {

	@XmlElement(name = "required-parameters", required = true)
	protected MachineEvent.RequiredParameters requiredParameters;
	@XmlElement(name = "extra-parameters", required = true)
	protected MachineEvent.ExtraParameters extraParameters;
	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlAttribute(name = "time")
	protected Long time;
	@XmlAttribute(name = "api-version")
	protected Integer apiVersion;
	@XmlAttribute(name = "request-id")
	protected String requestId;
	@XmlAttribute(name = "volatile")
	protected Boolean _volatile;

	/**
	 * Gets the value of the requiredParameters property.
	 * 
	 * @return possible object is {@link MachineEvent.RequiredParameters }
	 * 
	 */
	public MachineEvent.RequiredParameters getRequiredParameters() {
		return requiredParameters;
	}

	/**
	 * Sets the value of the requiredParameters property.
	 * 
	 * @param value allowed object is {@link MachineEvent.RequiredParameters }
	 * 
	 */
	public void setRequiredParameters(MachineEvent.RequiredParameters value) {
		this.requiredParameters = value;
	}

	/**
	 * Gets the value of the extraParameters property.
	 * 
	 * @return possible object is {@link MachineEvent.ExtraParameters }
	 * 
	 */
	public MachineEvent.ExtraParameters getExtraParameters() {
		return extraParameters;
	}

	/**
	 * Sets the value of the extraParameters property.
	 * 
	 * @param value allowed object is {@link MachineEvent.ExtraParameters }
	 * 
	 */
	public void setExtraParameters(MachineEvent.ExtraParameters value) {
		this.extraParameters = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the time property.
	 * 
	 * @return possible object is {@link Long }
	 * 
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * Sets the value of the time property.
	 * 
	 * @param value allowed object is {@link Long }
	 * 
	 */
	public void setTime(Long value) {
		this.time = value;
	}

	/**
	 * Gets the value of the apiVersion property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getApiVersion() {
		return apiVersion;
	}

	/**
	 * Sets the value of the apiVersion property.
	 * 
	 * @param value allowed object is {@link Integer }
	 * 
	 */
	public void setApiVersion(Integer value) {
		this.apiVersion = value;
	}

	/**
	 * Gets the value of the requestId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the value of the requestId property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setRequestId(String value) {
		this.requestId = value;
	}

	/**
	 * Gets the value of the volatile property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public Boolean isVolatile() {
		return _volatile;
	}

	/**
	 * Sets the value of the volatile property.
	 * 
	 * @param value allowed object is {@link Boolean }
	 * 
	 */
	public void setVolatile(Boolean value) {
		this._volatile = value;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="parameter" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;simpleContent>
	 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
	 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/extension>
	 *             &lt;/simpleContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "parameter" })
	public static class ExtraParameters implements Serializable {

		protected List<MachineEvent.ExtraParameters.Parameter> parameter;

		/**
		 * Gets the value of the parameter property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the parameter property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getParameter().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link MachineEvent.ExtraParameters.Parameter }
		 * 
		 * 
		 */
		public List<MachineEvent.ExtraParameters.Parameter> getParameter() {
			if (parameter == null) {
				parameter = new ArrayList<MachineEvent.ExtraParameters.Parameter>();
			}
			return this.parameter;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 * 
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;simpleContent>
		 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
		 *       &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/extension>
		 *   &lt;/simpleContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class Parameter implements Serializable {

			@XmlValue
			protected String value;
			@XmlAttribute(name = "key", required = true)
			protected String key;
			@XmlAttribute(name = "type")
			protected String type;

			/**
			 * Gets the value of the value property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getValue() {
				return value;
			}

			/**
			 * Sets the value of the value property.
			 * 
			 * @param value allowed object is {@link String }
			 * 
			 */
			public void setValue(String value) {
				this.value = value;
			}

			/**
			 * Gets the value of the key property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getKey() {
				return key;
			}

			/**
			 * Sets the value of the key property.
			 * 
			 * @param value allowed object is {@link String }
			 * 
			 */
			public void setKey(String value) {
				this.key = value;
			}

			/**
			 * Gets the value of the type property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getType() {
				return type;
			}

			/**
			 * Sets the value of the type property.
			 * 
			 * @param value allowed object is {@link String }
			 * 
			 */
			public void setType(String value) {
				this.type = value;
			}

		}

	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="parameter" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;simpleContent>
	 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
	 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/extension>
	 *             &lt;/simpleContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "parameter" })
	public static class RequiredParameters implements Serializable {

		protected List<MachineEvent.RequiredParameters.Parameter> parameter;

		/**
		 * Gets the value of the parameter property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the parameter property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getParameter().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link MachineEvent.RequiredParameters.Parameter }
		 * 
		 * 
		 */
		public List<MachineEvent.RequiredParameters.Parameter> getParameter() {
			if (parameter == null) {
				parameter = new ArrayList<MachineEvent.RequiredParameters.Parameter>();
			}
			return this.parameter;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 * 
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;simpleContent>
		 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
		 *       &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/extension>
		 *   &lt;/simpleContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class Parameter implements Serializable {

			@XmlValue
			protected String value;
			@XmlAttribute(name = "key", required = true)
			protected String key;
			@XmlAttribute(name = "type")
			protected String type;

			/**
			 * Gets the value of the value property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getValue() {
				return value;
			}

			/**
			 * Sets the value of the value property.
			 * 
			 * @param value allowed object is {@link String }
			 * 
			 */
			public void setValue(String value) {
				this.value = value;
			}

			/**
			 * Gets the value of the key property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getKey() {
				return key;
			}

			/**
			 * Sets the value of the key property.
			 * 
			 * @param value allowed object is {@link String }
			 * 
			 */
			public void setKey(String value) {
				this.key = value;
			}

			/**
			 * Gets the value of the type property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getType() {
				return type;
			}

			/**
			 * Sets the value of the type property.
			 * 
			 * @param value allowed object is {@link String }
			 * 
			 */
			public void setType(String value) {
				this.type = value;
			}

		}

	}

}
