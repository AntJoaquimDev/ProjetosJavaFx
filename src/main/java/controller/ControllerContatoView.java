package controller;

import agendaDao.ComboBoxGenericDao;
import agendaDao.CrudGenecDao;
import agendaModel.Cidade;
import agendaModel.Contato;
import agendaModel.TipoContato;
import agendaUtil.Alerta;
import agendaUtil.MascarsCampo;
import agendaUtil.ValidarCampo;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerContatoView implements Initializable, IntCadastro {

    @FXML
    private JFXDatePicker dtNascimento;
    @FXML
    private ToggleGroup sexo;
    @FXML
    private JFXRadioButton rbFemenino;
    @FXML
    private RadioButton rbMasculino;

    @FXML
    private JFXCheckBox checkedAtivo;

    @FXML
    private Label lblTitulo;
    @FXML
    private JFXTextField tfUf;

    @FXML
    private JFXTextField TfCep;

    @FXML
    private JFXTextField tfTelef1;

    @FXML
    private JFXTextField tfNascimento;

    @FXML
    private JFXTextField tfTelef2;

    @FXML
    private JFXTextField tfId;

    @FXML
    private JFXTextField tfDescricao;
    @FXML
    private JFXTextField tfEndereco;

    @FXML
    private JFXTextField tfNum;

    @FXML
    private TableView<Contato> tbView;
    @FXML
    private ComboBox<Cidade> cboxCidade; //carregar listCidade banco usando a class generic
    @FXML
    private ComboBox<TipoContato> cboxTipoContato;  // carrega da obslist

    @FXML
    private Button btnNovo;
    @FXML
    private Button btnSlvar;

    @FXML
    private Button btnDeletar;

    @FXML
    private TextField tfPesquisar;

    @FXML
    private JFXTextField txfEmail;


    @FXML
    void clicartabela(MouseEvent event) {
        seteCamposFormes();
    }

    @FXML
    void pesquisarRegistro(ActionEvent event) {
        atualizarTabela();
    }

    @FXML
    void moverTabela(KeyEvent event) {
        seteCamposFormes();
    }

    @FXML
    void filtrarRegistro(KeyEvent event) {
        atualizarTabela();
        seteCamposFormes();
    }


    // imp para acessar os mtd tipoContatodao
    private ComboBoxGenericDao<TipoContato> comboBoxTipoContatodao = new ComboBoxGenericDao();
    private ComboBoxGenericDao<Cidade> comboBoxCidadeodao = new ComboBoxGenericDao();

    private CrudGenecDao<Contato> dao = new CrudGenecDao<>();
    private List<Contato> lista;
    private ObservableList<Contato> observableList = FXCollections.observableArrayList();
    private Contato objetoSelecionado = new Contato();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblTitulo.setText("Cadastro Contato");
        cboxTipoContato.setItems(comboBoxTipoContatodao.comboBox("TipoContato"));
        cboxCidade.setItems(comboBoxCidadeodao.comboBox("Cidade"));
        criarColunasTabela();
        atualizarTabela();
        //seteCamposFormes();
        //carregar ocombox de uf e cep
        cboxCidade.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tfUf.setText(cboxCidade.getSelectionModel().getSelectedItem().getUf());
                TfCep.setText(cboxCidade.getSelectionModel().getSelectedItem().getCep());
            }
        });
        mascaCampos();
    }

    @FXML
    void salvarRegistro(ActionEvent event) {
        if (ValidarCampo.chegcarcampoVazio(tfDescricao, tfEndereco, cboxCidade, cboxTipoContato, tfNum,
               tfTelef1)) {

            Contato contato = new Contato(); // criar contato A049
            if (objetoSelecionado != null) {
                contato.setId(objetoSelecionado.getId());   // apenas isso para alterar contato
            }

            contato.setDescricao(tfDescricao.getText());
            contato.setEndereco(tfEndereco.getText());
            contato.setNumero(Integer.parseInt(tfNum.getText()));
            contato.setCidade(cboxCidade.getSelectionModel().getSelectedItem());
            contato.setTipoContato(cboxTipoContato.getSelectionModel().getSelectedItem());
            contato.setEmail(txfEmail.getText());
            contato.setTelefone1(Long.parseLong(tfTelef1.getText()));
            contato.setTelefone2(Long.parseLong(tfTelef2.getText()));
            LocalDate dataNascimento = dtNascimento.getValue(); // converter data nascimento
            contato.setNascimento(dataNascimento);

            if (checkedAtivo.isSelected()) {
                contato.setAtivo(true);
            } else {
                contato.setAtivo(false);
            }

            if (rbMasculino.isSelected()) {
                contato.setSexo("M");
            } else {
                contato.setSexo("F");
            }
            //salvando o contato no banco
            if (dao.salvar(contato)) {
                Alerta.msgInformacao("Registro gravado com Sucesso ");
                atualizarTabela();
            } else {
                Alerta.msgInformacao("Ocorreu errro ao Gravar registro ");
            }
        }
    }

    @FXML
    void deletarRegistro(ActionEvent event) {

        if (Alerta.msgConfimarExclusao(tfDescricao.getText())) {
            dao.excluir(objetoSelecionado);
            limparCamposFormes();
            atualizarTabela();
            Alerta.msgInformacao("Registro foi excluido com Sucesso.");
        }

    }

    @FXML
    void pesquisarRegistro(KeyEvent event) {
        atualizarTabela();
    }

    @FXML
    void atualizarListaClick(MouseEvent event) {
        atualizarTabela();
    }

    @FXML
    void incluirRegistro(ActionEvent event) {
        limparCamposFormes();
    }

    @Override
    public void criarColunasTabela() {

        TableColumn<Contato, Long> colunaId = new TableColumn<>("ID");
        TableColumn<Contato, String> colunaDescricao = new TableColumn<>("Nome do Contato");
        TableColumn<Contato, TipoContato> colunaTContato = new TableColumn<>("Tipo Contato");
        TableColumn<Contato, Cidade> colunaCidade = new TableColumn<>("Cidade");
        TableColumn<Contato, LocalDate> colunaNascimento = new TableColumn<>("Nascimento");


        tbView.getColumns().addAll(colunaId, colunaDescricao, colunaTContato, colunaCidade, colunaNascimento);//

        colunaId.setCellValueFactory(new PropertyValueFactory("id"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory("descricao"));
        colunaTContato.setCellValueFactory(new PropertyValueFactory("tipoContato"));
        colunaCidade.setCellValueFactory(new PropertyValueFactory("cidade"));
        colunaNascimento.setCellValueFactory(new PropertyValueFactory("nascimento"));

        //Redimensionamento manual--> tamanhos tem q somar 0.100
        colunaId.prefWidthProperty().bind(tbView.widthProperty().multiply(0.05));
        colunaDescricao.prefWidthProperty().bind(tbView.widthProperty().multiply(0.4));
        colunaTContato.prefWidthProperty().bind(tbView.widthProperty().multiply(0.2));
        colunaCidade.prefWidthProperty().bind(tbView.widthProperty().multiply(0.2));
        colunaNascimento.prefWidthProperty().bind(tbView.widthProperty().multiply(0.15));

        //formatdor de data DateTimeFormater
        DateTimeFormatter formatar = DateTimeFormatter.ofPattern("dd/MM/YYYY");

        colunaNascimento.setCellFactory(tc -> new TableCell<Contato, LocalDate>() {
            @Override
            protected void updateItem(LocalDate data, boolean empty) {
                super.updateItem(data, empty);
                if (data != null) {
                    setText(formatar.format(data));
                }
            }
        });
    }

    @Override
    public void atualizarTabela() {
        observableList.clear();
        lista = dao.consultar(tfPesquisar.getText(), "Contato");
        for (Contato c : lista) {
            observableList.addAll(c);
        }
        tbView.getItems().setAll(observableList);
        tbView.getSelectionModel().selectFirst();

    }

    @Override
    public void seteCamposFormes() {
        //pegar o objeto selecionado e preencher o form pelo index;;;;

        if (!tbView.getItems().isEmpty()) {
            objetoSelecionado = tbView.getItems().get(tbView.getSelectionModel().getSelectedIndex());
            tfId.setText(String.valueOf(objetoSelecionado.getId()));
            tfDescricao.setText(objetoSelecionado.getDescricao());
            tfEndereco.setText(objetoSelecionado.getEndereco());
            tfNum.setText(String.valueOf(objetoSelecionado.getNumero()));
            tfTelef1.setText(String.valueOf(objetoSelecionado.getTelefone1()));
            tfTelef2.setText(String.valueOf(objetoSelecionado.getTelefone2()));
            txfEmail.setText(objetoSelecionado.getEmail());
            checkedAtivo.setSelected(objetoSelecionado.isAtivo());

            if (objetoSelecionado.getSexo().equals("M")) {
                rbMasculino.setSelected(true);
            } else {
                rbFemenino.setSelected(true);
            }
            dtNascimento.setValue(objetoSelecionado.getNascimento());

            //tem q instaciar um novo objeto tipoContto e cidade
            TipoContato tipoContatoSelecionado = new TipoContato();
            tipoContatoSelecionado.setId(objetoSelecionado.getTipoContato().getId());
            tipoContatoSelecionado.setDescricaoTipo(objetoSelecionado.getTipoContato().getDescricaoTipo());
            cboxTipoContato.getSelectionModel().selectFirst(); // selecionar no primeiro registro
            cboxTipoContato.setValue(tipoContatoSelecionado);

            Cidade cidadeSelecionada = new Cidade();
            cidadeSelecionada.setId(objetoSelecionado.getCidade().getId());
            cidadeSelecionada.setDescricao(objetoSelecionado.getCidade().getDescricao());
            cidadeSelecionada.setUf(objetoSelecionado.getCidade().getUf());
            cidadeSelecionada.setCep(objetoSelecionado.getCidade().getCep());
            cboxCidade.getSelectionModel().selectFirst();
            cboxCidade.setValue(cidadeSelecionada);

        }
    }

    @Override
    public void limparCamposFormes() {
        objetoSelecionado = null;
        tfId.clear();
        tfDescricao.clear();
        tfEndereco.clear();
        tfNum.clear();
        tfTelef1.clear();
        tfTelef2.clear();
        txfEmail.clear();
        tfUf.clear();
        TfCep.clear();


        rbMasculino.setSelected(true);
        checkedAtivo.setSelected(true);

        cboxCidade.getSelectionModel().select(-1);
        cboxTipoContato.getSelectionModel().select(-1);
        tfDescricao.requestFocus();
    }
    public void mascaCampos(){
        MascarsCampo.mskNumero(tfNum);
        MascarsCampo.mskNumero(tfTelef1);
        MascarsCampo.mskNumero(tfTelef2);

    }
}
