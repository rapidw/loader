import React, { useEffect, useRef, useState } from 'react';
import {
  Button,
  Card,
  ConfigProvider,
  Empty,
  Form,
  InputNumber,
  notification,
  Transfer,
  Upload,
} from 'antd';
import { PoweroffOutlined, SettingOutlined, UploadOutlined } from '@ant-design/icons/lib';
import { useRequest } from '@umijs/hooks';
import { TransferItem } from 'antd/es/transfer';
import ReactResizeDetector from 'react-resize-detector';
import 'ace-builds';
import AceEditor from 'react-ace';
import 'ace-builds/webpack-resolver';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-monokai';
import 'ace-builds/src-noconflict/ext-language_tools';
import { UploadFile } from 'antd/es/upload/interface';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import styles from './NewTest.less';

const customizeRenderEmpty = () => (
  <div style={{ textAlign: 'center' }}>
    {Empty.PRESENTED_IMAGE_SIMPLE}
    <p>No Data Found</p>
  </div>
);
export interface Store {
  [name: string]: any;
}

interface EditorPops {
  value?: string;
  onChange?: (value: string) => void;
}

const OptionsEditor: React.FC<EditorPops> = ({ value = '', onChange }) => {
  const [text, setText] = useState<string>(value);

  useEffect(() => {
    setText(value);
  }, [value]);

  const editor = useRef<AceEditor>(null);
  const div = useRef<HTMLDivElement>(null);

  const onEditorChange = (newValue: string) => {
    setText(newValue);
    if (onChange) {
      onChange(newValue);
    }
  };
  return (
    <div style={{ border: '1px solid #eee', height: '300px', width: '100%' }} ref={div}>
      <ReactResizeDetector
        handleWidth
        handleHeight
        onResize={() => {
          if (editor.current) {
            editor.current.editor.resize();
          }
        }}
        targetDomEl={div.current!}
      />

      <AceEditor
        mode="json"
        theme="monokai"
        width="100%"
        height="100%"
        fontSize={14}
        showPrintMargin={false}
        showGutter
        highlightActiveLine
        value={text}
        setOptions={{
          enableBasicAutocompletion: true,
          showLineNumbers: true,
          tabSize: 2,
        }}
        onChange={onEditorChange}
        ref={editor}
      />
    </div>
  );
};

interface SupervisorTransferProps {
  value?: Array<string>;
  onChange?: (value: Array<string>) => void;
}

const SupervisorTransfer: React.FC<SupervisorTransferProps> = ({ value = [], onChange }) => {
  const [targetSupervisors, setTargetSupervisors] = useState<Array<string>>(value);

  useEffect(() => {
    setTargetSupervisors(value);
  }, [JSON.stringify(value)]);

  const supervisorListRequest = useRequest('/api/supervisors', {
    formatResult: response => {
      const data: TransferItem[] = [];

      response.data.data.forEach((v: any) => {
        data.push({
          key: v.id,
          title: `${v.host}:${v.path}`,
        });
      });

      return data;
    },
  });

  const onTransferChange = (targetKeys: string[]) => {
    setTargetSupervisors(targetKeys);
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
        render={item => (item.title != null ? item.title : '')}
      />
    </ConfigProvider>
  );
};

// xl: 768p
// xxl: 1080p and above
const formItemLayout = {
  labelCol: {
    xl: { span: 4 },
    xxl: { span: 5 },
  },
  wrapperCol: {
    xl: { span: 20 },
    xxl: { span: 14 },
  },
};

const buttonLayout = {
  wrapperCol: {
    xl: { span: 24 },
    xxl: { span: 19 },
  },
};

const tailLayout = {
  wrapperCol: { span: 24 },
};

const NewTest: React.FC = () => {
  const [form] = Form.useForm();

  const { loading, run } = useRequest(
    (values: Store) => {
      const formData = new FormData();
      formData.append(
        'config',
        JSON.stringify({
          throughputLimit: values.throughputLimit,
          durationLimit: values.durationLimit,
          perAgentTotalLimit: values.perAgentTotalLimit,
          supervisors: values.supervisors,
        }),
      );
      if (values.masterOptions) {
        formData.append('masterOptions', values.masterOptions);
      }
      if (values.agentOptions) {
        formData.append('agentOptions', values.agentOptions);
      }
      if (values.agentConfig) {
        formData.append('agentConfig', values.agentConfig.file);
      }
      if (values.agentJar) {
        formData.append('agentJar', values.agentJar.file);
      }
      return {
        url: '/api/start',
        method: 'post',
        data: formData,
        requestType: 'form',
      };
    },
    {
      manual: true,
      onSuccess: () => {
        notification.success({ message: 'test started' });
      },
    },
  );

  const onFinish = (values: Store) => {
    run(values);
  };

  const handleReuse = () => {
    form.setFieldsValue({
      throughputLimit: 1,
      durationLimit: 1,
      perAgentTotalLimit: 1,
      supervisors: [0],
      masterOptions: '{"a":1}',
      agentOptions: '{"a":1}',
    });
  };

  const onLimitChange = () => {
    form.validateFields(['throughputLimit', 'durationLimit', 'perAgentTotalLimit']);
  };

  const isEmpty = (str: string): boolean => str === undefined || str === null || str === '';

  const [agentConfigFileList, setAgentConfigFileList] = useState<Array<UploadFile>>([]);
  const beforeAgentConfigUpload = (file: UploadFile) => {
    setAgentConfigFileList([file]);
    return false;
  };
  const [agentJarFileList, setAgentJarFileList] = useState<Array<UploadFile>>([]);
  const beforeAgentJarUpload = (file: UploadFile) => {
    setAgentJarFileList([file]);
    return false;
  };

  return (
    <PageHeaderWrapper>
      <Card>
        <Form {...formItemLayout} form={form} name="new-test-form" onFinish={onFinish}>
          <Form.Item {...buttonLayout} style={{ textAlign: 'right' }}>
            <Button type="primary" onClick={handleReuse}>
              <SettingOutlined />
              Re-use last test options
            </Button>
          </Form.Item>
          <Form.Item
            name="throughputLimit"
            label="Throughput Limit(RPS)"
            rules={[
              { type: 'number', min: 1 },
              ({ getFieldValue }: any) => ({
                validator(rule: any, value: any) {
                  if (
                    isEmpty(value) &&
                    isEmpty(getFieldValue('perAgentTotalLimit')) &&
                    isEmpty(getFieldValue('durationLimit'))
                  ) {
                    return Promise.reject(new Error('at least ONE limit should be specified'));
                  }
                  return Promise.resolve();
                },
              }),
            ]}
          >
            <InputNumber onChange={onLimitChange} />
          </Form.Item>
          <Form.Item
            name="durationLimit"
            label="Duration Limit(ms)"
            rules={[
              { type: 'number', min: 1 },
              ({ getFieldValue }: any) => ({
                validator(rule: any, value: any) {
                  if (
                    isEmpty(value) &&
                    isEmpty(getFieldValue('throughputLimit')) &&
                    isEmpty(getFieldValue('perAgentTotalLimit'))
                  ) {
                    return Promise.reject(new Error('at least ONE limit should be specified'));
                  }
                  return Promise.resolve();
                },
              }),
            ]}
          >
            <InputNumber onChange={onLimitChange} />
          </Form.Item>
          <Form.Item
            name="perAgentTotalLimit"
            label="per Agent Total Limit"
            rules={[
              { type: 'number', min: 1 },
              ({ getFieldValue }: any) => ({
                validator(rule: any, value: any) {
                  if (
                    isEmpty(value) &&
                    isEmpty(getFieldValue('throughputLimit')) &&
                    isEmpty(getFieldValue('durationLimit'))
                  ) {
                    return Promise.reject(new Error('at least ONE limit should be specified'));
                  }
                  return Promise.resolve();
                },
              }),
            ]}
          >
            <InputNumber onChange={onLimitChange} />
          </Form.Item>
          <Form.Item name="supervisors" label="Supervisors" required>
            <SupervisorTransfer />
          </Form.Item>
          <Form.Item name="masterOptions" label="Master Options" required>
            <OptionsEditor />
          </Form.Item>
          <Form.Item name="agentOptions" label="Agent Options" required>
            <OptionsEditor />
          </Form.Item>
          <Form.Item name="agentConfig" label="Agent Config" required>
            <Upload
              fileList={agentConfigFileList}
              beforeUpload={beforeAgentConfigUpload}
              className={styles.customUpload}
            >
              <Button>
                <UploadOutlined /> Upload
              </Button>
            </Upload>
          </Form.Item>
          <Form.Item name="agentJar" label="Agent Jar" required>
            <Upload
              fileList={agentJarFileList}
              beforeUpload={beforeAgentJarUpload}
              className={styles.customUpload}
            >
              <Button>
                <UploadOutlined /> Upload
              </Button>
            </Upload>
          </Form.Item>
          <Form.Item {...tailLayout} style={{ textAlign: 'center' }}>
            <Button type="primary" htmlType="submit" icon={<PoweroffOutlined />} loading={loading}>
              START
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </PageHeaderWrapper>
  );
};

export default NewTest;
