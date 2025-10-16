package app.subd.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ComboBoxSearchListener implements EventHandler<KeyEvent> {

    private final ComboBox<String> comboBox;
    private final ObservableList<String> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public ComboBoxSearchListener(final ComboBox<String> comboBox) {
        this.comboBox = comboBox;
        data = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.setVisibleRowCount(7);
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(ComboBoxSearchListener.this);
        ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<>(this.comboBox);
        comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            }
        });
        this.comboBox.setSkin(comboBoxListViewSkin);
    }

    @Override
    public void handle(KeyEvent event) {

        if(event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.DOWN) {
            if(!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            this.comboBox.setVisibleRowCount(7);
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if(event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            this.comboBox.setVisibleRowCount(7);
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB
                || event.getCode() == KeyCode.SHIFT || event.getCode() == KeyCode.CONTROL) {
            return;
        }

        ObservableList<java.lang.String> list = FXCollections.observableArrayList();
        for (String datum : data) {
            if (datum.toLowerCase().contains(
                    ComboBoxSearchListener.this.comboBox
                            .getEditor().getText().toLowerCase())) {
                list.add(datum);
            }
        }
        String t = comboBox.getEditor().getText();

        comboBox.setItems(list);
        comboBox.getEditor().setText(t);
        if(!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if(!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

}