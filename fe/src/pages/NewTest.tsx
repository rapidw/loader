import React, {useEffect, useRef, useState} from "react";
import {Button, Card, ConfigProvider, Empty, Form, InputNumber, Modal, notification, Transfer, Upload} from "antd";
import {CopyOutlined, PoweroffOutlined, SettingOutlined, UploadOutlined} from "@ant-design/icons/lib";
import {useRequest} from "@umijs/hooks";
import {TransferItem} from "antd/es/transfer";
import ReactResizeDetector from "react-resize-detector"
import "ace-builds";
import AceEditor from "react-ace";
import ReactAce from "react-ace";
import "ace-builds/webpack-resolver";
import "ace-builds/src-noconflict/mode-json"
import "ace-builds/src-noconflict/theme-monokai";
import 'ace-builds/src-noconflict/ext-language_tools';
import {UploadFile} from "antd/es/upload/interface";
import styles from './NewTest.less';
import {PageHeaderWrapper} from "@ant-design/pro-layout";


const customizeRenderEmpty = () => (
  <div style={{textAlign: 'center'}}>
    {Empty.PRESENTED_IMAGE_SIMPLE}
    <p>No Data Found</p>
  </div>
);


interface EditorPops {
  value?: string;
  onChange?: (value: string) => void;
}

const OptionsEditor: React.FC<EditorPops> = ({value = "", onChange}) => {
  const [text, setText] = useState<string>(value);

  useEffect(() => {
    setText(value);
  }, [value]);

  const editor = useRef<ReactAce>(null);

  const onEditorChange = (value: string) => {
    setText(value);
    if (onChange) {
      onChange(value)
    }
  };
  return (<div style={{
      border: "1px solid #eee",
      height: "300px",
      width: "100%",
    }}
    >

      <ReactResizeDetector
        handleWidth
        handleHeight
        onResize={() => {
          if (editor.current) {
            editor.current.editor.resize();
          }
        }}
        targetDomEl={editor.current}
      />

      <AceEditor
        mode="json"
        theme="monokai"
        width={"100%"}
        height={"100%"}
        fontSize={14}
        showPrintMargin={false}
        showGutter={true}
        highlightActiveLine={true}
        value={text}
        setOptions={{
          enableBasicAutocompletion: true,
          enableLiveAutocompletion: false,
          enableSnippets: false,
          showLineNumbers: true,
          tabSize: 2,
        }}
        onChange={onEditorChange}

        ref={editor}
      />

    </div>
  )
};

interface AgentConfig {
  fileList?: Array<UploadFile>;
  json?: string;
}

interface AgentConfigUploaderPops {
  value?: AgentConfig;
  onChange?: (value: AgentConfig) => void;
}

const AgentConfigUploader: React.FC<AgentConfigUploaderPops> = ({value = {}, onChange}) => {
  const [fileList, setFileList] = useState<Array<UploadFile>>(value.fileList!);
  const [json, setJson] = useState(value?.json);

  const [modalState, setModalState] = useState({visible: false});
  const [modalWidth, setModalWidth] = useState();
  const modalEditor = useRef<ReactAce>(null);

  const handleOk = () => {
    setModalState({...modalState, visible: false})
  };
  const showModal = () => {
    setModalState({...modalState, visible: true});
  };

  const onJsonChange = (json: string) => {
    setJson(json);
    if (onChange) {
      onChange({json: json})
    }
  };

  const beforeUpload = (file: UploadFile) => {
    setFileList([file]);
    if (onChange) {
      onChange({fileList: [file]})
    }
    return false
  };

  return (
    <div style={{display: "flex"}}>
      <Upload
        fileList={fileList}
        beforeUpload={beforeUpload}
        showUploadList={false}
      >
        <Button>
          <UploadOutlined/> Upload
        </Button>
      </Upload>
      <span style={{marginLeft: "10px", marginRight: "10px", alignSelf: "center"}}> or </span>
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

                // console.log(modalEditor.current);
                if (modalEditor.current) {
                  // console.log("resize");
                  modalEditor.current.editor.resize();
                  setModalWidth(width + 48);
                }
              }}>

              <AceEditor
                mode="json"
                theme="monokai"
                // width={modalWidth}
                fontSize={14}
                showPrintMargin={false}
                showGutter={true}
                highlightActiveLine={true}
                value={json}
                setOptions={{
                  enableBasicAutocompletion: false,
                  enableLiveAutocompletion: false,
                  enableSnippets: false,
                  showLineNumbers: true,
                  tabSize: 2,
                }}
                onChange={onJsonChange}
                ref={modalEditor}
              />
            </ReactResizeDetector>
          </div>
        </Modal>

      </div>
    </div>
  )
};


interface SupervisorTransferProps {
  value?: Array<string>;
  onChange?: (value: Array<string>) => void;
}

const SupervisorTransfer: React.FC<SupervisorTransferProps> = ({value = [], onChange}) => {

  const [targetSupervisors, setTargetSupervisors] = useState<Array<string>>(value);
  useEffect(() => {
    setTargetSupervisors(value);
  }, [value]);

  const supervisorListRequest = useRequest("/api/supervisors", {
    formatResult: (response) => {
      let data: TransferItem[] = [];

      response.data.data.forEach((v: any) => {
        data.push({
          key: v.id,
          title: `${v.host}:${v.path}`
        })
      });

      return data
    }
  });

  const onTransferChange = (targetKeys: string[]) => {
    setTargetSupervisors(targetKeys)
    if (onChange) {
      onChange(targetKeys);
    }
  };


  return (
    <ConfigProvider renderEmpty={customizeRenderEmpty}>
      <Transfer
        dataSource={supervisorListRequest.data}
        onChange={onTransferChange}
        targetKeys={targetSupervisors}
        titles={['Available', 'Selected']}
        className={styles.customTransfer}
        showSelectAll
        render={item => item.title != null ? item.title : ""}
      />
    </ConfigProvider>
  );
};


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
  const [form] = Form.useForm();

  const {loading, run} = useRequest((values: any) => {
    const formData = new FormData();
    formData.append("json", JSON.stringify({
      throughputLimit: values.throughputLimit,
      durationLimit: values.durationLimit,
      perAgentTotalLimit: values.perAgentTotalLimit,
    }));
    if (values.masterOptions) {
      formData.append("masterOptions", values.masterOptions);
    }
    if (values.agentOptions) {
      formData.append("agentOptions", values.agentOptions);
    }
    if (values.agentConfig.json) {
      formData.append("agentConfig", values.agentConfig.json);
    }
    if (values.agentConfig.fileList) {
      formData.append("agentConfig", values.agentConfig.fileList[0])
    }
    return (
      {
        url: "/api/start",
        method: "post",
        data: formData,
        requestType: "form"
      })
  }, {
    manual: true,
    onSuccess: data => {
      notification.success({message: "test started"})
    }
  });

  const onFinish = values => {
    console.log(values);
    run(values)
  };

  const handleReuse = () => {
    form.setFieldsValue({
      throughputLimit: 1,
      durationLimit: 1,
      perAgentTotalLimit: 1,
      supervisors: [0],
      masterOptions: "{\"a\":1}",
      agentOptions: "{\"a\":1}",
      agentConfig: {
        json: "{\"a\":1}"
      }
    });
  };

  const onLimitChange = () => {
    form.validateFields(["throughputLimit", "durationLimit", "perAgentTotalLimit"])
  };

  const isEmpty = (str: string): boolean => {
    return str == undefined || str === "";
  }

  return (
    <PageHeaderWrapper>
      <Card>
        <Form
          {...formItemLayout}
          form={form}
          name="new-test-form"
          onFinish={onFinish}
        >
          <Form.Item {...buttonLayout} style={{textAlign: "right"}}>
            <Button type="primary" onClick={handleReuse}><SettingOutlined/>Re-use last test options</Button>
          </Form.Item>
          <Form.Item name="throughputLimit"
                     label="Throughput Limit(RPS)"
                     rules={[{type: "number", min: 1},
                       ({getFieldValue}: any) => ({
                         validator(rule: any, value: any) {
                           if (isEmpty(value)
                             && isEmpty(getFieldValue("perAgentTotalLimit"))
                             && isEmpty(getFieldValue("durationLimit"))) {
                             return Promise.reject("at least ONE limit should be specified");
                           } else {
                             return Promise.resolve();
                           }
                         }
                       })
                     ]}>
            <InputNumber onChange={onLimitChange}/>
          </Form.Item>
          <Form.Item name="durationLimit"
                     label="Duration Limit(ms)"
                     rules={[{type: "number", min: 1},
                       ({getFieldValue}: any) => ({
                         validator(rule: any, value: any) {
                           if (isEmpty(value)
                             && isEmpty(getFieldValue("throughputLimit"))
                             && isEmpty(getFieldValue("perAgentTotalLimit"))) {
                             return Promise.reject("at least ONE limit should be specified");
                           } else {
                             return Promise.resolve();
                           }
                         }
                       })
                     ]}>
            <InputNumber onChange={onLimitChange}/>
          </Form.Item>
          <Form.Item name="perAgentTotalLimit"
                     label="per Agent Total Limit"
                     rules={[{type: "number", min: 1},
                       ({getFieldValue}: any) => ({
                         validator(rule: any, value: any) {
                           if (isEmpty(value)
                             && isEmpty(getFieldValue("throughputLimit"))
                             && isEmpty(getFieldValue("durationLimit"))) {
                             return Promise.reject("at least ONE limit should be specified");
                           } else {
                             return Promise.resolve();
                           }
                         }
                       })
                     ]}>
            <InputNumber onChange={onLimitChange}/>
          </Form.Item>
          <Form.Item name="supervisors" label="Supervisors" required>
            <SupervisorTransfer/>
          </Form.Item>
          <Form.Item name="masterOptions" label="Master Options" required>
            <OptionsEditor/>
          </Form.Item>
          <Form.Item name="agentOptions" label="Agent Options" required>
            <OptionsEditor/>
          </Form.Item>
          <Form.Item name="agentConfig" label="Agent Config" required>
            <AgentConfigUploader/>
          </Form.Item>
          <Form.Item {...tailLayout} style={{textAlign: "center"}}>
            <Button type="primary" htmlType="submit" icon={<PoweroffOutlined/>} loading={loading}>
              START
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </PageHeaderWrapper>
  );
};

export default NewTest
