import React, {useState} from "react";
import {
  Badge,
  Button,
  Card,
  Checkbox,
  Col,
  ConfigProvider,
  Empty,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
  Table,
} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import {useForm} from "antd/es/form/util";
import {useRequest} from "@umijs/hooks";
import request from 'umi-request';
import {ExclamationCircleOutlined, ToolOutlined} from "@ant-design/icons/lib";
import {CheckboxChangeEvent} from "antd/es/checkbox";

const {confirm} = Modal;

interface SupervisorInfo {
  id: number,
  host: string,
  port: number,
  path: string,
  status: string,
}

const customizeRenderEmpty = () => (
  <div style={{textAlign: 'center'}}>
    {Empty.PRESENTED_IMAGE_SIMPLE}
    <p>No Data Found</p>
  </div>
);

const SupervisorList: React.FC = () => {

  const removeSingleSupervisorRequest = useRequest((record: SupervisorInfo) => ({
    url: `/api/supervisors/${record.id}`,
    method: "delete",
  }), {
    manual: true,
    onSuccess: (result, params) => {
      if (result.errorCode == 0) {
        message.success(`new supervisor ${params[0].host}:${params[0].port} removed`);
        supervisorTableRequest.refresh();
      }
    }
  });

  const removeMultiSupervisorRequest = useRequest((data: { ids: number[] }) => ({
    url: `/api/supervisors`,
    method: "delete",
    data: data
  }), {
    manual: true,
    onSuccess: (result, params) => {
      if (result.errorCode == 0) {
        message.success(`selected supervisors has been removed`);
        supervisorTableRequest.refresh();
      }
    }
  });

  const columns = [
    {
      title: "ID",
      dataIndex: "id"
    },
    {
      title: "Status",
      dataIndex: "status",
      render: (text: string) => {
        switch (text) {
          case "READY":
            return <Badge status={"success"} text={text}/>;
          case "RUNNING":
            return <Badge status={"processing"} text={text}/>;
          case "ERRORED":
            return <Badge status={"error"} text={text}/>
          default:
            return <Badge status={"warning"} text={"UNKNOWN"}/>
        }
      }
    },
    {
      title: 'Host',
      dataIndex: 'host',
    },
    {
      title: 'Port',
      dataIndex: 'port',
    },
    {
      title: "Path",
      dataIndex: "path"
    },
    {
      title: "Action",
      render: (_: any, record: SupervisorInfo) => (
        <span>
          <a onClick={() => removeSingleConfirm(record)}>Remove</a>
        </span>
      )
    }
  ];

  const getSupervisorTableData = () => request.get('/api/supervisors')
    .then(function (response) {
      return {
        total: response.data.total,
        list: response.data.data
      }
    })
  ;

  const supervisorTableRequest = useRequest(({current, pageSize, sorter: s, filters: f}) => {
    const p: any = {current, pageSize};
    if (s?.field && s?.order) {
      p[s.field] = s.order;
    }
    if (f) {
      Object.entries(f).forEach(([filed, value]) => {
        p[filed] = value;
      });
    }
    // console.log(p);
    return getSupervisorTableData();
  }, {
    paginated: true,
    defaultPageSize: 1,
    pollingInterval: 3000
  });

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);

  const onSelectChange = (selectedRowKeys: any) => {
    setSelectedRowKeys(selectedRowKeys);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectChange,
  };
  const hasSelected = selectedRowKeys.length > 0;

  const [addSupervisorModalVisible, setAddSupervisorModalVisible] = useState(false);

  const onDeploySupervisorButtonClicked = () => {
    setAddSupervisorModalVisible(true)
  };

  const [addSupervisorForm] = useForm();

  const onAddSupervisorModalCancel = () => {
    setAddSupervisorModalVisible(false);
  };

  const addSupervisorInitialValues = {
    port: 22,
    path: "~"
  };

  const addSupervisorRequest = useRequest((addSupervisorForm) => ({
    url: "/api/supervisors",
    method: "post",
    data: addSupervisorForm.getFieldsValue()

  }), {
    manual: true,
    onSuccess: (result, params) => {
      if (result.errorCode == 0) {
        setAddSupervisorModalVisible(false);
        message.success(`new supervisor ${params[0].getFieldValue("host")}:${params[0].getFieldValue("port")} deployed`);
        supervisorTableRequest.refresh()
      }
    }
  });

  const onAddSupervisorModalOk = () => {

    addSupervisorForm.validateFields().then(value => {
      addSupervisorRequest.run(addSupervisorForm)

    }).catch(info => console.log("validate failed", info));
  };

  const removeSingleConfirm = (record: SupervisorInfo) => {
    confirm({
      title: 'Are you sure remove this supervisor?',
      icon: <ExclamationCircleOutlined/>,
      content: 'this supervisor will exit',
      okText: 'Yes',
      okType: 'danger',
      cancelText: 'No',
      onOk() {
        removeSingleSupervisorRequest.run(record);
      },
      onCancel() {

      },
    });
  };

  const removeMultiConfirm = () => {
    confirm({
      title: 'Are you sure remove these supervisors?',
      icon: <ExclamationCircleOutlined/>,
      content: 'selected supervisors will exit',
      okText: 'Yes',
      okType: 'danger',
      cancelText: 'No',
      onOk() {
        setSelectedRowKeys([]);
        removeMultiSupervisorRequest.run({
          ids: selectedRowKeys
        });
      },
      onCancel() {

      },
    });
  };

  const onAutoRefreshChange = (e: CheckboxChangeEvent) => {

    if (e.target.checked) {
      supervisorTableRequest.refresh();
    } else {
      supervisorTableRequest.cancel();
    }
  };

  return (
    <PageHeaderWrapper>
      <Card>
        <Row justify={"space-between"}>
          <Col>
            <div style={{display: "inline-block"}}>
              <Button onClick={removeMultiConfirm} disabled={!hasSelected}>
                Remove
              </Button>

              <span style={{marginLeft: 8}}>
            {hasSelected ? `Selected ${selectedRowKeys.length} items` : ''}
            </span>
            </div>
          </Col>

          <Col>
            <Checkbox defaultChecked={true} onChange={onAutoRefreshChange}>Auto Refresh</Checkbox>
            <Button type="primary" onClick={onDeploySupervisorButtonClicked}><ToolOutlined/>Deploy Supervisor</Button>
            <Modal
              title="Supervisor Deploy Options"
              visible={addSupervisorModalVisible}
              onOk={onAddSupervisorModalOk}
              onCancel={onAddSupervisorModalCancel}
              okButtonProps={{loading: addSupervisorRequest.loading}}
              // wrapProps={{style: {pointerEvents: "none"}}}
            >
              <Form form={addSupervisorForm}
                    labelCol={{lg: 5}}
                    wrapperCol={{lg: 18}}
                    initialValues={addSupervisorInitialValues}
                // onFinish={onAddSupervisorFinish}
              >
                <Form.Item name="host" label="Host" required>
                  <Input/>
                </Form.Item>
                <Form.Item name="port" label="Port" required rules={[{type: "number", max: 65535, min: 1}]}>
                  <InputNumber/>
                </Form.Item>
                <Form.Item name="path" label="Deploy path" required>
                  <Input/>
                </Form.Item>
                <Form.Item name="username" label="Username" required>
                  <Input/>
                </Form.Item>
                <Form.Item name="password" label="Password" required>
                  <Input/>
                </Form.Item>
              </Form>
            </Modal>
          </Col>
        </Row>
        <Row style={{marginTop: "16px"}}>
          <Col span={24}>
            <ConfigProvider renderEmpty={customizeRenderEmpty}>
              <Table rowSelection={rowSelection} columns={columns}
                     rowKey={"id"} {...supervisorTableRequest.tableProps} />
            </ConfigProvider>
          </Col>
        </Row>
      </Card>
    </PageHeaderWrapper>
  )
}

export default SupervisorList;
