package org.firstinspires.ftc.teamcode.hrvhs;

public class DoublyLinkedListElement {
    private DoublyLinkedListElement m_next;
    private DoublyLinkedListElement m_previous;
    private ACommand m_data;

    public void setData(ACommand newData) {
        m_data = newData;
    }

    public ACommand getData() {
        return m_data;
    }

    public DoublyLinkedListElement getNext() {
        return m_next;
    }

    public DoublyLinkedListElement getPrevious() {
        return m_previous;
    }

    public void add(DoublyLinkedListElement listElement) {
        if (m_next == null) {
            m_next = listElement;
            m_next.m_previous = this;
        } else {
            m_next.m_previous = listElement;
            listElement.m_next = m_next;
            listElement.m_previous = this;
            m_next = listElement;
        }
    }

    public DoublyLinkedListElement remove() {
        if (m_previous == null && m_next == null) {
            // no-op
        } else if (m_next == null) {
            m_previous.m_next = null;
        } else if (m_previous == null) {
            m_next.m_previous = null;
        } else {
            m_next.m_previous = m_previous;
            m_previous.m_next = m_next;
        }
        DoublyLinkedListElement returnNext = m_next;
        m_next = null;
        m_previous = null;
        return returnNext;
    }
}
