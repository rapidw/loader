import React, {useRef, useState} from "react";
import {Button, Card, Checkbox, Col, Form, Input, Modal, Row, Select, Transfer, Typography, Upload} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import ReactResizeDetector from 'react-resize-detector';
import AceEditor from 'react-ace';
import {CopyOutlined, UploadOutlined} from "@ant-design/icons/lib";
import styles from "./NewTest.less";
import useDimensions from 'react-use-dimensions';

const {Title} = Typography;
const {Option} = Select;

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 5 },

  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 },
    md: { span: 18 },
    lg: { span: 12 },
    xl: { span: 16 },
    xxl:{ span: 12},
  },
};

const buttonLayout = {
  wrapperCol: {
    xs: { span: 24},
    sm: { span: 21},
    md: { span: 23},
    lg: { span: 17},
    xl: { span: 21},
    xxl: {span: 17},
  }
};

const tailLayout = {
  wrapperCol: {span: 24},
};

const mockData = [];
for (let i = 0; i < 20; i++) {
  mockData.push({
    key: i.toString(),
    title: `content${i + 1}rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr`,
    description: `description of content${i + 1}`,
    disabled: i % 3 < 1,
  });
}

const NewTest: React.FC = () => {

  const [MasterOptions, setMasterOptions] = useState("");

  const [editorWidth, setEditorWidth] = useState("");

  const children = [];
  for (let i = 10; i < 36; i++) {
    children.push(<Option key={i.toString(36) + i}>{i.toString(36) + i}</Option>);
  }

  const [modalState, setModalState] = useState({visible: false});
  const [modalWidth, setModalWidth] = useState();

  const [form] = Form.useForm();

  const onGenderChange = value => {
    form.setFieldsValue({
      note: `Hi, ${value === 'male' ? 'man' : 'lady'}!`,
    });
  };

  const onFinish = values => {
    console.log(values);
  };

  const onReset = () => {
    form.resetFields();
  };

  const onFill = () => {
    form.setFieldsValue({
      note: 'Hello world!',
      gender: 'male',
    });
  };

  const editor1 = useRef();
  const editor2 = useRef();
  const modalEditor = useRef();
  // const item = useRef();

  const [item, {width}] = useDimensions();
  // console.log("width: " + width);

  const showModal = () => {
    setModalState({...modalState, visible: true});
  };

  const handleOk = () => {
    setModalState({...modalState, visible: false})
  };

  const handleReload = () => {
    console.log("dddd");
  };

  window.onresize = () => {
    // console.log(item)
    // if (editor1.current && item.current){

      // console.log(item.current);
      // console.log(editor1.current);
      setEditorWidth(width + "px")
      // console.log("resize");

      // setEditorWidth("100%")
      // editor1.current.editor.resize({width: width})
      // editor1.current.editor.layout({width: item.current.clientWidth});
    // }
  };

  return (
    <PageHeaderWrapper>
      <Card>
        <Typography>
          <Title level={3}>
            Test Options
          </Title>
        </Typography>

        <Form {...formItemLayout} form={form} name="control-hooks" onFinish={onFinish}>
          <Form.Item {...buttonLayout} style={{textAlign:"right"}}>
            <Button type="primary" onClick={handleReload}>Re-use last test options</Button>
          </Form.Item>
          <Form.Item name="throughLimit" label="Throughput Limit(RPS)">
            <Input placeholder="0 means no limit" ref={item}/>
          </Form.Item>
          <Form.Item name="durationLimit" label="Duration Limit(ms)">
            <Input placeholder="0 means no limit"/>
          </Form.Item>
          <Form.Item name="perAgentTotalLimit" label="per Agent Total Limit">
            <Input placeholder="0 means no limit"/>
          </Form.Item>
          <Form.Item name="agents" label="Select Agents" >
            <Transfer
              dataSource={mockData}
              titles={['Available', 'Selected']}
              className={styles.customTransfer}
              // targetKeys={targetKeys}
              // selectedKeys={selectedKeys}
              // onChange={this.handleChange}
              // onSelectChange={this.handleSelectChange}
              // onScroll={this.handleScroll}
              render={item => item.title}
              // disabled={disabled}
              // listStyle={{width: "auto"}}
              // style={{width:"50%", flex: "none"}}
              // ref={item}
            />
          </Form.Item>
          <Form.Item label="Master Options" required>
            <div style={{border: "1px solid #eee", resize: "both", overflow: "auto", height: "300px", width: editorWidth}}>
              <ReactResizeDetector
                handleWidth
                handleHeight
                onResize={() => {
                  if (editor1.current) {
                    editor1.current.editor.resize();
                  }
                }}
              >
                  <AceEditor
                    // placeholder="Input Your Options Here"
                    mode="json"
                    theme="github"
                    // width={editorWidth + "px"}
                    // width={size.width}
                    // height={size.height}
                    // name="blah2"
                    // onLoad={this.onLoad}
                    // onChange={this.onChange}
                    fontSize={14}
                    showPrintMargin={false}
                    showGutter={true}
                    highlightActiveLine={true}
                    value={MasterOptions}
                    setOptions={{
                      enableBasicAutocompletion: false,
                      enableLiveAutocompletion: false,
                      enableSnippets: false,
                      showLineNumbers: true,
                      tabSize: 2,
                    }}

                    // className={styles.customEditor}
                    // ref={editor1}
                  />
              </ReactResizeDetector>
            </div>
          </Form.Item>

          {/* ------------------------------------------------------------------------------------------------------------------*/}

          <Form.Item label="Agent Options" required>
            <div style={{border: "1px solid #eee", resize: "both", overflow: "auto", height: "300px", width: editorWidth}}>
              <ReactResizeDetector
                handleWidth
                handleHeight
                onResize={() => {
                  if (editor2.current) {
                    editor2.current.editor.resize();
                  }
                }}
              >
                <AceEditor
                  // placeholder="Input Your Options Here"
                  mode="json"
                  theme="github"
                  // width={editorWidth + "px"}
                  // width={size.width}
                  // height={size.height}
                  // name="blah2"
                  // onLoad={this.onLoad}
                  // onChange={this.onChange}
                  fontSize={14}
                  showPrintMargin={false}
                  showGutter={true}
                  highlightActiveLine={true}
                  value={MasterOptions}
                  setOptions={{
                    enableBasicAutocompletion: false,
                    enableLiveAutocompletion: false,
                    enableSnippets: false,
                    showLineNumbers: true,
                    tabSize: 2,
                  }}

                  // className={styles.customEditor}
                  // ref={editor1}
                />
              </ReactResizeDetector>
            </div>
          </Form.Item>

          <Form.Item label="Agent Config" required>
            <Row>
              <Col>
                <Upload>
                  <Button>
                    <UploadOutlined/> Upload
                  </Button>
                </Upload>
              </Col>
              <Col>
                <span style={{marginLeft: "10px", marginRight: "10px"}}> or </span>
              </Col>
              <Col>
                <div>
                  <Button onClick={showModal}>
                    <CopyOutlined/> Paste
                  </Button>
                  <Modal
                    title="Agent Config"
                    visible={modalState.visible}
                    onOk={handleOk}
                    onCancel={handleOk}
                    width={modalWidth}
                    wrapProps={{style: {pointerEvents: "none"}}}
                  >
                    <div style={{
                      border: "1px solid #eee",
                      resize: "both",
                      overflow: "auto",
                      height: "300px",
                      width: "300px"
                    }}>
                      <ReactResizeDetector
                        handleWidth
                        handleHeight
                        onResize={(width) => {
                          if (modalEditor.current) {
                            console.log(width)
                            modalEditor.current.editor.resize();
                            setModalWidth(width + 48)
                          }
                        }}
                      >
                      <AceEditor
                        // placeholder="Input Your Options Here"
                        mode="json"
                        theme="github"
                        // width={editorWidth + "px"}
                        // width={size.width}
                        // height={size.height}
                        // name="blah2"
                        // onLoad={this.onLoad}
                        // onChange={this.onChange}
                        fontSize={14}
                        showPrintMargin={false}
                        showGutter={true}
                        highlightActiveLine={true}
                        value={MasterOptions}
                        setOptions={{
                          enableBasicAutocompletion: false,
                          enableLiveAutocompletion: false,
                          enableSnippets: false,
                          showLineNumbers: true,
                          tabSize: 2,
                        }}

                        // className={styles.customEditor}
                        ref={modalEditor}
                      />
                      </ReactResizeDetector>
                    </div>
                  </Modal>

                </div>
              </Col>

            </Row>
          </Form.Item>


          <Form.Item {...tailLayout} style={{textAlign: "center"}}>
            <Button type="primary" htmlType="submit">
              Submit
            </Button>
          </Form.Item>
        </Form>

      </Card>
    </PageHeaderWrapper>
  );
};

export default NewTest
