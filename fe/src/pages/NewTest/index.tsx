import React, {useState} from "react";
import { Form, Card, Select, Input, Button } from "antd";
import { PageHeaderWrapper } from '@ant-design/pro-layout';

const { Option } = Select;

const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 8 },
};
const tailLayout = {
  wrapperCol: { offset: 8, span: 8 },
};

const NewTest: React.FC = () => {
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

  return (
    <PageHeaderWrapper {...layout.wrapperCol}>
      <Card>
        <Form {...layout} form={form} name="control-hooks" onFinish={onFinish}>
        {/*<Form form={form} name="control-hooks" onFinish={onFinish}>*/}
          <Form.Item name="note" label="Note" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="gender" label="Gender" rules={[{ required: true }]}>
            <Select
              placeholder="Select a option and change input text above"
              onChange={onGenderChange}
              allowClear
            >
              <Option value="male">male</Option>
              <Option value="female">female</Option>
              <Option value="other">other</Option>
            </Select>
          </Form.Item>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => prevValues.gender !== currentValues.gender}
          >
            {({ getFieldValue }) => {
              return getFieldValue('gender') === 'other' ? (
                <Form.Item name="customizeGender" label="Customize Gender" rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
              ) : null;
            }}
          </Form.Item>
          <Form.Item {...tailLayout} style={{ textAlign: 'right' }}>
            <Button type="primary" htmlType="submit">
              Submit
            </Button>
            <Button htmlType="button" onClick={onReset}>
              Reset
            </Button>
            <Button type="link" htmlType="button" onClick={onFill}>
              Fill form
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </PageHeaderWrapper>
  );
}

export default NewTest
