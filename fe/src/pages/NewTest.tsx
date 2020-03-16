import React, {useRef, useState} from "react";
import {Button, Card, Col, ConfigProvider, Empty, Form, InputNumber, Modal, Row, Select, Transfer, Upload} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import {CopyOutlined, UploadOutlined} from "@ant-design/icons/lib";
import styles from "./NewTest.less";
import ReactResizeDetector from "react-resize-detector"
import AceEditor from "react-ace";
import ReactAce from "react-ace";
import {useRequest} from "@umijs/hooks";
import {TransferItem} from "antd/es/transfer";

const {Option} = Select;

const customizeRenderEmpty = () => (
  <div style={{textAlign: 'center'}}>
    {Empty.PRESENTED_IMAGE_SIMPLE}
    <p>No Data Found</p>
  </div>
);

// xl: 768p
// xxl: 1080p and above
const formItemLayout = {
  labelCol: {
    xl: {span: 4},
    xxl: {span: 5}

  },
  wrapperCol: {
    xl: {span: 20},
    xxl: {span: 14},
  },
};

const buttonLayout = {
  wrapperCol: {
    xl: {span: 24},
    xxl: {span: 19},
  }
};

const tailLayout = {
  wrapperCol: {span: 24},
};


const NewTest: React.FC = () => {

  const [MasterOptions, setMasterOptions] = useState("");

  const [targetSupervisors, setTargetSupervisors] = useState<string[]>([]);

  const supervisorListRequest= useRequest("/api/supervisors", {
    formatResult: (response) => {
      let data:TransferItem[] = [];

      response.data.data.forEach( (v:any) => {
        data.push({
          key: v.id,
          title: `${v.host}:${v.path}`
        })
      });

      return data
    }
  });

  const children = [];
  for (let i = 10; i < 36; i++) {
    children.push(<Option key={i.toString(36) + i}>{i.toString(36) + i}</Option>);
  }

  const [modalState, setModalState] = useState({visible: false});
  const [modalWidth, setModalWidth] = useState();


  const [form] = Form.useForm();

  const onFinish = values => {
    console.log(values);
  };

  const editor1 = useRef<ReactAce>(null);
  const editor2 = useRef<ReactAce>(null);
  const modalEditor = useRef<ReactAce>(null);

  // const []
  const showModal = () => {
    setModalState({...modalState, visible: true});
  };

  const handleOk = () => {
    setModalState({...modalState, visible: false})
  };

  const handleReuse = () => {
    console.log("dddd");
  };

  const onEditor1Change = (value: string) => {
    setMasterOptions(value);
  };

  return (
    <PageHeaderWrapper>
      <Card>
        <Form
          {...formItemLayout}
          form={form}
          name="new-task-form"
          onFinish={onFinish}
        >
          <Form.Item {...buttonLayout} style={{textAlign: "right"}}>
            <Button type="primary" onClick={handleReuse}>Re-use last test options</Button>
          </Form.Item>
          <Form.Item name="throughLimit" label="Throughput Limit(RPS)" rules={[{type: "number", min: 0}]}>
            <InputNumber value={10}/>
          </Form.Item>
          <Form.Item name="durationLimit" label="Duration Limit(ms)" rules={[{type: "number", min: 0}]}>
            <InputNumber defaultValue={0}/>
          </Form.Item>
          <Form.Item name="perAgentTotalLimit" label="per Agent Total Limit" rules={[{type: "number", min: 0}]}>
            <InputNumber defaultValue={0}/>
          </Form.Item>
          <Form.Item name="supervisors" label="Supervisors">
            <ConfigProvider renderEmpty={customizeRenderEmpty}>
            <Transfer
              dataSource={supervisorListRequest.data}
              onChange={(targetKeys) => setTargetSupervisors(targetKeys)}
              targetKeys={targetSupervisors}
              titles={['Available', 'Selected']}
              className={styles.customTransfer}
              showSelectAll
              // targetKeys={targetKeys}
              // selectedKeys={selectedKeys}
              // onChange={this.handleChange}
              // onSelectChange={this.handleSelectChange}
              // onScroll={this.handleScroll}
              render={item => item.title != null ? item.title : ""}
              // style={{height: "900px"}}
            />
            </ConfigProvider>
          </Form.Item>
          <Form.Item label="Master Options" required>
            <div style={{
              border: "1px solid #eee",
              height: "300px",
              width: "100%",
            }}
            >

              <ReactResizeDetector
                handleWidth
                handleHeight
                onResize={() => {
                  if (editor1.current) {
                    editor1.current.editor.resize();
                  }
                  if (editor2.current) {
                    editor2.current.editor.resize();
                  }
                }}
                targetDomEl={editor1.current}
              />

              <AceEditor
                mode="json"
                theme="github"
                width={"100%"}
                height={"100%"}
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
                onChange={onEditor1Change}

                ref={editor1}
              />

            </div>
          </Form.Item>

          {/* ------------------------------------------------------------------------------------------------------------------*/}

          <Form.Item label="Agent Options" required>
            <div
              style={{border: "1px solid #eee", height: "300px", width: "100%"}}>
              <AceEditor
                mode="json"
                theme="github"
                width={"100%"}
                height={"100%"}
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
                ref={editor2}
              />
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
                    <div
                      style={{
                        border: "1px solid #eee",
                        resize: "both",
                        overflow: "auto",
                        height: "300px",
                        width: "500px"

                      }}
                    >
                      <ReactResizeDetector
                        handleWidth
                        handleHeight
                        onResize={(width: number) => {

                          console.log(modalEditor.current);
                          if (modalEditor.current) {
                            console.log("resize");
                            modalEditor.current.editor.resize();
                            setModalWidth(width + 48);
                          }
                        }}>

                        <AceEditor
                          mode="json"
                          theme="github"
                          // width={modalWidth}
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
